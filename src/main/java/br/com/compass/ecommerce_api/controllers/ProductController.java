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
import br.com.compass.ecommerce_api.projections.ProductProjection;
import br.com.compass.ecommerce_api.services.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ProductResponseDto> save(@Valid @RequestBody ProductSaveDto dto) {
        Product product = productService.save(ProductMapper.toProduct(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(ProductMapper.toDto(product));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CLIENT')")
    public ResponseEntity<ProductResponseDto> findById(@PathVariable Long id) {
        Product product = productService.findById(id);
        return ResponseEntity.ok(ProductMapper.toDto(product));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        productService.deactivateById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/deactivate/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deactivateById(@PathVariable Long id) {
        productService.deactivateById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/update-name/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> updateName(@PathVariable Long id, @RequestBody @Valid ProductNameUpdateDto dto) {
        productService.updateName(id, dto.getName());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/update-description/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> updateDescription(@PathVariable Long id, @RequestBody @Valid ProductDescriptionUpdateDto dto) {
        productService.updateDescription(id, dto.getDescription());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/update-amount/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> updateAmount(@PathVariable Long id, @RequestBody @Valid ProductAmountUpdateDto dto) {
        productService.updateAmount(id, dto.getAmount());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<PageableDto<ProductProjection>> findAll(@PageableDefault(size = 3) Pageable pageable) {
        Page<ProductProjection> products = productService.findAll(pageable);
        return ResponseEntity.ok(PageableMapper.toDto(products));
    }
}
