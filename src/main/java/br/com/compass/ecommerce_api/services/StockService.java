package br.com.compass.ecommerce_api.services;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.compass.ecommerce_api.entities.Product;
import br.com.compass.ecommerce_api.entities.Stock;
import br.com.compass.ecommerce_api.exceptions.InsufficientStockException;
import br.com.compass.ecommerce_api.exceptions.ProductUniqueViolationException;
import br.com.compass.ecommerce_api.projections.StockProjection;
import br.com.compass.ecommerce_api.repositories.StockRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class StockService {

    private final StockRepository stockRepository;
    private final ProductService productService;

    @Transactional
    public Stock save(Long id, Stock stock) {
        try {
            Product product = productService.findById(id);
            stock.setProduct(product);
            return stockRepository.save(stock);
        } catch (DataIntegrityViolationException ex) {
            throw new ProductUniqueViolationException(
                String.format("Product of id {%d} already registered", id)
            );
        }
    }

    @Transactional(readOnly = true)
    public Stock findById(Long id) {
        return stockRepository.findById(id).orElseThrow(
            () -> new EntityNotFoundException(String.format("Entry {%d} not found in stock", id))
        );
    }

    @Transactional
    public void add(Long id, Integer quantity) {
        Stock stock = findById(id);
        stock.setQuantity(stock.getQuantity() + quantity);
    }

    @Transactional
    public void decrease(Long id, Integer quantity) {
        Stock stock = findById(id);

        if (quantity > stock.getQuantity()) {
            throw new InsufficientStockException("Not enough stock to decrease");
        }

        stock.setQuantity(stock.getQuantity() - quantity);
    }

    @Transactional(readOnly = true)
    public Page<StockProjection> findAll(Pageable pageable) {
        return stockRepository.findAllPageable(pageable);
    }
}
