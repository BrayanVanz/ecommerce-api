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
import br.com.compass.ecommerce_api.projections.UserProjection;
import br.com.compass.ecommerce_api.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDto> save(@Valid @RequestBody UserSaveDto dto) {
        User newUser = userService.save(UserMapper.toUser(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.toDto(newUser));
    }

    @PostMapping("/admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserResponseDto> saveAdmin(@Valid @RequestBody UserSaveDto dto) {
        User newUser = UserMapper.toUser(dto);
        newUser.setRole(UserRole.ADMIN);
        userService.save(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.toDto(newUser));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') OR ( hasAuthority('CLIENT') AND #id == authentication.principal.id )")
    public ResponseEntity<UserResponseDto> findById(@PathVariable Long id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(UserMapper.toDto(user));
    }

    @PostMapping("/password-reset/request")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CLIENT')")
    public ResponseEntity<Void> requestPasswordReset(@RequestBody @Valid UserEmailDto dto) {
        userService.initiatePasswordReset(dto.getEmail());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/password-reset/confirm")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CLIENT')")
    public ResponseEntity<Void> confirmPasswordReset(
        @RequestParam String token,
        @Valid @RequestBody PasswordResetDto dto) {

        userService.resetPassword(token, dto.getNewPassword(), dto.getConfirmedPassword());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<PageableDto<UserProjection>> findAll(@PageableDefault(size = 3) Pageable pageable) {
        Page<UserProjection> users = userService.findAll(pageable);
        return ResponseEntity.ok(PageableMapper.toDto(users));
    }
}
