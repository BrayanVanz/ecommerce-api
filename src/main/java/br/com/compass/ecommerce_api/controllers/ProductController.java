package br.com.compass.ecommerce_api.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.compass.ecommerce_api.dtos.PageableDto;
import br.com.compass.ecommerce_api.dtos.ProductAmountUpdateDto;
import br.com.compass.ecommerce_api.dtos.ProductDescriptionUpdateDto;
import br.com.compass.ecommerce_api.dtos.ProductNameUpdateDto;
import br.com.compass.ecommerce_api.dtos.ProductResponseDto;
import br.com.compass.ecommerce_api.dtos.ProductSaveDto;
import br.com.compass.ecommerce_api.dtos.mappers.PageableMapper;
import br.com.compass.ecommerce_api.dtos.mappers.ProductMapper;
import br.com.compass.ecommerce_api.entities.Product;
import br.com.compass.ecommerce_api.exceptions.ErrorMessage;
import br.com.compass.ecommerce_api.projections.ProductProjection;
import br.com.compass.ecommerce_api.services.ProductService;
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

@Tag(name = "Products", description = "Performs product related operations")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Creates a new product", description = "Requires Bearer Token. Access restricted to ADMIN", 
        security = @SecurityRequirement(name = "security"),
        responses = {
            @ApiResponse(responseCode = "201", description = "Resource created successfully", 
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "User doesn't have permission to access resource",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "409", description = "Product name has already been registered",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "422", description = "Invalid input data",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
        }
    )
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ProductResponseDto> save(@Valid @RequestBody ProductSaveDto dto) {
        Product product = productService.save(ProductMapper.toProduct(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(ProductMapper.toDto(product));
    }

    @Operation(summary = "Retrieves a product by its id", description = "Requires Bearer Token", 
        security = @SecurityRequirement(name = "security"),
        responses = {
            @ApiResponse(responseCode = "200", description = "Resource retireved successfully", 
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
        }
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CLIENT')")
    public ResponseEntity<ProductResponseDto> findById(@PathVariable Long id) {
        Product product = productService.findById(id);
        return ResponseEntity.ok(ProductMapper.toDto(product));
    }

    @Operation(summary = "Deletes a product by its id", description = "Requires Bearer Token. Access restricted to ADMIN", 
        security = @SecurityRequirement(name = "security"),
        responses = {
            @ApiResponse(responseCode = "204", description = "Resource deleted successfully"), 
            @ApiResponse(responseCode = "403", description = "User doesn't have permission to access resource",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "409", description = "Product cannot be deleted",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
        }
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        productService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Deactivate a product by its id", description = "Requires Bearer Token. Access restricted to ADMIN", 
        security = @SecurityRequirement(name = "security"),
        responses = {
            @ApiResponse(responseCode = "204", description = "Resource deactivated successfully"), 
            @ApiResponse(responseCode = "403", description = "User doesn't have permission to access resource",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
        }
    )
    @PatchMapping("/deactivate/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deactivateById(@PathVariable Long id) {
        productService.deactivateById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Updates product name", description = "Requires Bearer Token. Access restricted to ADMIN", 
        security = @SecurityRequirement(name = "security"),
        responses = {
            @ApiResponse(responseCode = "204", description = "Name successfully updated"), 
            @ApiResponse(responseCode = "403", description = "User doesn't have permission to access resource",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "409", description = "Product name has already been registered",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "422", description = "Invalid input data",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
        }
    )
    @PatchMapping("/update-name/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> updateName(@PathVariable Long id, @RequestBody @Valid ProductNameUpdateDto dto) {
        productService.updateName(id, dto.getName());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Updates product description", description = "Requires Bearer Token. Access restricted to ADMIN", 
        security = @SecurityRequirement(name = "security"),
        responses = {
            @ApiResponse(responseCode = "204", description = "Description successfully updated"), 
            @ApiResponse(responseCode = "403", description = "User doesn't have permission to access resource",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "422", description = "Invalid input data",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
        }
    )
    @PatchMapping("/update-description/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> updateDescription(@PathVariable Long id, @RequestBody @Valid ProductDescriptionUpdateDto dto) {
        productService.updateDescription(id, dto.getDescription());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Updates product amount", description = "Requires Bearer Token. Access restricted to ADMIN", 
        security = @SecurityRequirement(name = "security"),
        responses = {
            @ApiResponse(responseCode = "204", description = "Amount successfully updated"), 
            @ApiResponse(responseCode = "403", description = "User doesn't have permission to access resource",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "422", description = "Invalid input data",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
        }
    )
    @PatchMapping("/update-amount/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> updateAmount(@PathVariable Long id, @RequestBody @Valid ProductAmountUpdateDto dto) {
        productService.updateAmount(id, dto.getAmount());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Retrieves best selling products", description = "Requires Bearer Token. Access restricted to ADMIN", 
        security = @SecurityRequirement(name = "security"),
        parameters = {
            @Parameter(in = ParameterIn.QUERY, name = "page",
                content = @Content(schema = @Schema(type = "integer", defaultValue = "0")),
                description = "Represents a returned page"
            ),
            @Parameter(in = ParameterIn.QUERY, name = "size",
                content = @Content(schema = @Schema(type = "integer", defaultValue = "3")),
                description = "Reperesents the amount of elements in a page"
            )
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "Resource retrieved successfully", 
                content = @Content(mediaType = "application/json", 
                array = @ArraySchema(schema = @Schema(implementation = ProductProjection.class)))
            ),
            @ApiResponse(responseCode = "403", description = "User doesn't have permission to access resource",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
        }
    )
    @GetMapping("/best-selling")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<PageableDto<ProductProjection>> findBestSelling(@PageableDefault(size = 3) Pageable pageable) {
        Page<ProductProjection> products = productService.findBestSelling(pageable);
        return ResponseEntity.ok(PageableMapper.toDto(products));
    }

    @Operation(summary = "Retrieves all products", description = "Requires Bearer Token. Access restricted to ADMIN", 
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
                array = @ArraySchema(schema = @Schema(implementation = ProductProjection.class)))
            ),
            @ApiResponse(responseCode = "403", description = "User doesn't have permission to access resource",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
        }
    )
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<PageableDto<ProductProjection>> findAll(@PageableDefault(size = 3) Pageable pageable) {
        Page<ProductProjection> products = productService.findAll(pageable);
        return ResponseEntity.ok(PageableMapper.toDto(products));
    }
}
