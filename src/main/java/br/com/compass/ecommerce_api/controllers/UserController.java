package br.com.compass.ecommerce_api.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.compass.ecommerce_api.dtos.PageableDto;
import br.com.compass.ecommerce_api.dtos.PasswordResetDto;
import br.com.compass.ecommerce_api.dtos.UserEmailDto;
import br.com.compass.ecommerce_api.dtos.UserResponseDto;
import br.com.compass.ecommerce_api.dtos.UserSaveDto;
import br.com.compass.ecommerce_api.dtos.mappers.PageableMapper;
import br.com.compass.ecommerce_api.dtos.mappers.UserMapper;
import br.com.compass.ecommerce_api.entities.User;
import br.com.compass.ecommerce_api.enums.UserRole;
import br.com.compass.ecommerce_api.exceptions.ErrorMessage;
import br.com.compass.ecommerce_api.projections.UserProjection;
import br.com.compass.ecommerce_api.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Users", description = "Performs user related operations")
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Creates a new user", description = "Resource used to create a new user", 
        responses = {
            @ApiResponse(responseCode = "201", description = "Resource created successfully", 
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "User email has already been registered",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "422", description = "Invalid input data",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
        }
    )
    @PostMapping
    public ResponseEntity<UserResponseDto> save(@Valid @RequestBody UserSaveDto dto) {
        User newUser = userService.save(UserMapper.toUser(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.toDto(newUser));
    }

    @Operation(summary = "Creates a new admin", description = "Requires Bearer Token. Access restricted to ADMIN", 
        security = @SecurityRequirement(name = "security"),
        responses = {
            @ApiResponse(responseCode = "201", description = "Resource created successfully", 
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "User doesn't have permission to access resource",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "409", description = "Admin email has already been registered",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "422", description = "Invalid input data",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
        }
    )
    @PostMapping("/admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserResponseDto> saveAdmin(@Valid @RequestBody UserSaveDto dto) {
        User newUser = UserMapper.toUser(dto);
        newUser.setRole(UserRole.ADMIN);
        userService.save(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.toDto(newUser));
    }

    @Operation(summary = "Retrieves a user by their id", description = "Requires Bearer Token. Access restricted to own account", 
        security = @SecurityRequirement(name = "security"),
        responses = {
            @ApiResponse(responseCode = "200", description = "Resource retireved successfully", 
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "User doesn't have permission to access resource",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
        }
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') OR ( hasAuthority('CLIENT') AND #id == authentication.principal.id )")
    public ResponseEntity<UserResponseDto> findById(@PathVariable Long id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(UserMapper.toDto(user));
    }

    @Operation(summary = "Requests password reset", description = "Requires Bearer Token. Access restricted to own account", 
        security = @SecurityRequirement(name = "security"),
        responses = {
            @ApiResponse(responseCode = "200", description = "Request sent successfully"), 
            @ApiResponse(responseCode = "403", description = "User doesn't have permission to access resource",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "422", description = "Invalid input data",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
        }
    )
    @PostMapping("/password-reset/request")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CLIENT') AND #dto.email == authentication.principal.username")
    public ResponseEntity<Void> requestPasswordReset(@RequestBody @Valid UserEmailDto dto) {
        userService.initiatePasswordReset(dto.getEmail());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Confirms password reset", description = "Requires Bearer Token. Access restricted to own account", 
        security = @SecurityRequirement(name = "security"),
        parameters = {
                @Parameter(in = ParameterIn.QUERY, name = "token",
                content = @Content(schema = @Schema(type = "string")),
                description = "Represents a temporary, one use UUID token")
        },
        responses = {
            @ApiResponse(responseCode = "204", description = "Password successfully updated"), 
            @ApiResponse(responseCode = "400", description = "Incorrect password or token information",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "422", description = "Invalid input data",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
        }
    )
    @PatchMapping("/password-reset/confirm")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CLIENT')")
    public ResponseEntity<Void> confirmPasswordReset(
        @RequestParam String token,
        @Valid @RequestBody PasswordResetDto dto) {

        userService.resetPassword(token, dto.getNewPassword(), dto.getConfirmedPassword());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Retrieves all users", description = "Requires Bearer Token. Access restricted to ADMIN", 
        security = @SecurityRequirement(name = "security"),
        parameters = {
            @Parameter(in = ParameterIn.QUERY, name = "page",
                content = @Content(schema = @Schema(type = "integer", defaultValue = "0")),
                description = "Represents a returned page"
            ),
            @Parameter(in = ParameterIn.QUERY, name = "size",
                content = @Content(schema = @Schema(type = "integer", defaultValue = "3")),
                description = "Reperesents the amount of elements in a page"
            ),
            @Parameter(in = ParameterIn.QUERY, name = "sort", hidden = true,
                array = @ArraySchema(schema = @Schema(type = "string", defaultValue = "id,asc")),
                description = "Represents the sorting type being used. Accepts multiple criteria"
            )
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "Resource retrieved successfully", 
                content = @Content(mediaType = "application/json", 
                array = @ArraySchema(schema = @Schema(implementation = UserProjection.class)))
            ),
            @ApiResponse(responseCode = "403", description = "User doesn't have permission to access resource",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
        }
    )
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<PageableDto<UserProjection>> findAll(@PageableDefault(size = 3) Pageable pageable) {
        Page<UserProjection> users = userService.findAll(pageable);
        return ResponseEntity.ok(PageableMapper.toDto(users));
    }
}
