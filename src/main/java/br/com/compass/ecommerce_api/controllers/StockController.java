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
import org.springframework.web.bind.annotation.RestController;

import br.com.compass.ecommerce_api.dtos.PageableDto;
import br.com.compass.ecommerce_api.dtos.StockAlterDto;
import br.com.compass.ecommerce_api.dtos.StockResponseDto;
import br.com.compass.ecommerce_api.dtos.StockSaveDto;
import br.com.compass.ecommerce_api.dtos.mappers.PageableMapper;
import br.com.compass.ecommerce_api.dtos.mappers.StockMapper;
import br.com.compass.ecommerce_api.entities.Stock;
import br.com.compass.ecommerce_api.projections.StockProjection;
import br.com.compass.ecommerce_api.services.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/stock")
@PreAuthorize("hasAuthority('ADMIN')")
public class StockController {

    private final StockService stockService;

    @PostMapping
    public ResponseEntity<StockResponseDto> save(@Valid @RequestBody StockSaveDto dto) {
        Stock stock = stockService.save(dto.getProductId(), StockMapper.toStock(dto));
        StockResponseDto responseDto = StockMapper.tDto(stock);
        responseDto.setProductId(stock.getProduct().getId());
        responseDto.setProductName(stock.getProduct().getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockResponseDto> findById(@PathVariable Long id) {
        Stock stock = stockService.findById(id);
        StockResponseDto responseDto = StockMapper.tDto(stock);
        responseDto.setProductId(stock.getProduct().getId());
        responseDto.setProductName(stock.getProduct().getName());
        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/add-stock/{id}")
    public ResponseEntity<Void> add(@PathVariable Long id, @Valid @RequestBody StockAlterDto dto) {
        stockService.add(id, dto.getQuantity());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/decrease-stock/{id}")
    public ResponseEntity<Void> decrease(@PathVariable Long id, @Valid @RequestBody StockAlterDto dto) {
        stockService.decrease(id, dto.getQuantity());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/low-stock")
    public ResponseEntity<PageableDto<StockProjection>> findLowStock(@PageableDefault(size = 3) Pageable pageable) {
        Page<StockProjection> lowStockItems = stockService.findLowStock(pageable);
        return ResponseEntity.ok(PageableMapper.toDto(lowStockItems));
    }

    @GetMapping
    public ResponseEntity<PageableDto<StockProjection>> findAll(@PageableDefault(size = 3) Pageable pageable) {
        Page<StockProjection> stocks = stockService.findAll(pageable);
        return ResponseEntity.ok(PageableMapper.toDto(stocks));
    }
}
