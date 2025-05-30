package br.com.compass.ecommerce_api.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.compass.ecommerce_api.dtos.CartItemSaveDto;
import br.com.compass.ecommerce_api.dtos.PageableDto;
import br.com.compass.ecommerce_api.dtos.mappers.CartItemMapper;
import br.com.compass.ecommerce_api.dtos.mappers.PageableMapper;
import br.com.compass.ecommerce_api.entities.CartItem;
import br.com.compass.ecommerce_api.exceptions.ErrorMessage;
import br.com.compass.ecommerce_api.projections.CartItemProjection;
import br.com.compass.ecommerce_api.services.CartItemService;
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

@Tag(name = "Cart", description = "Performs cart related operations")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/cart")
public class CartItemController {

    private final CartItemService cartItemService;

    @Operation(summary = "Adds product to cart", description = "Requires Bearer Token", 
        security = @SecurityRequirement(name = "security"),
        responses = {
            @ApiResponse(responseCode = "204", description = "Product added successfully"), 
            @ApiResponse(responseCode = "404", description = "Resource not found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "409", description = "Inactive product cannot be added to cart",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "422", description = "Invalid input data",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
        }
    )
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CLIENT')")
    public ResponseEntity<Void> addToCart(@Valid @RequestBody CartItemSaveDto dto) {
        CartItem cartItem = CartItemMapper.toCart(dto);
        cartItemService.addToCart(cartItem, dto.getUserId(), dto.getProductId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Retrieves cart", description = "Requires Bearer Token. Access restricted to own cart", 
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
                array = @ArraySchema(schema = @Schema(implementation = CartItemProjection.class)))
            ),
            @ApiResponse(responseCode = "403", description = "User doesn't have permission to access resource",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
        }
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CLIENT') AND #id == authentication.principal.id")
    public ResponseEntity<PageableDto<CartItemProjection>> findAll(@PathVariable Long id, @PageableDefault(size = 3) Pageable pageable) {
        Page<CartItemProjection> cart = cartItemService.getCart(id, pageable);
        return ResponseEntity.ok(PageableMapper.toDto(cart));
    }
}
