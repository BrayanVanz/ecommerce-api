package br.com.compass.ecommerce_api.controllers;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.compass.ecommerce_api.dtos.PageableDto;
import br.com.compass.ecommerce_api.dtos.mappers.PageableMapper;
import br.com.compass.ecommerce_api.projections.TopBuyerProjection;
import br.com.compass.ecommerce_api.services.PurchaseService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/purchases")
public class PurchaseController {

    private final PurchaseService purchaseService;

    @GetMapping("/total-amount")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<BigDecimal> getTotalAmount(@RequestParam String period) {
        BigDecimal totalAmount = purchaseService.getTotalAmount(period);
        return ResponseEntity.ok(totalAmount);
    }

    @GetMapping("/total-purchases")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Integer> getTotalPurchases(@RequestParam String period) {
        Integer totalPurchases = purchaseService.getTotalPurchases(period);
        return ResponseEntity.ok(totalPurchases);
    }

    @PostMapping("/perform-purchase/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CLIENT') AND #dto.email == authentication.principal.username")
    public ResponseEntity<Void> performPurchase(@PathVariable Long id) {
        purchaseService.performPurchase(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/top-buyers")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<PageableDto<TopBuyerProjection>> findTopBuyers(@PageableDefault(size = 3) Pageable pageable) {
        Page<TopBuyerProjection> topBuyers = purchaseService.findTopBuyers(pageable);
        return ResponseEntity.ok(PageableMapper.toDto(topBuyers));
    }
}
