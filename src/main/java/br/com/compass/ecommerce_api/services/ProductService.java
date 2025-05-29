package br.com.compass.ecommerce_api.services;

import java.math.BigDecimal;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.compass.ecommerce_api.entities.Product;
import br.com.compass.ecommerce_api.enums.ProductStatus;
import br.com.compass.ecommerce_api.exceptions.EntityNotFoundException;
import br.com.compass.ecommerce_api.exceptions.ProductDeletionNotAllowedException;
import br.com.compass.ecommerce_api.exceptions.ProductUniqueViolationException;
import br.com.compass.ecommerce_api.projections.ProductProjection;
import br.com.compass.ecommerce_api.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public Product save(Product product) {
        try {
            return productRepository.save(product);
        } catch (DataIntegrityViolationException ex) {
            throw new ProductUniqueViolationException(String.format("Product {%s} is already registered", product.getName()));
        }
    }

    @Transactional(readOnly = true)
    public Product findById(Long id) {
        return productRepository.findById(id).orElseThrow(
            () -> new EntityNotFoundException(String.format("Product {%d} not found", id))
        );
    }

    @Transactional
    public void deleteById(Long id) {
        Product product = findById(id);

        if (product.getTimesPurchased() > 0) {
            throw new ProductDeletionNotAllowedException(
                String.format("Cannot delete product {%d} as it has already been purchased", id)
            );
        }

        productRepository.deleteById(id);
    }

    @Transactional
    public void deactivateById(Long id) {
        Product product = findById(id);
        product.setStatus(ProductStatus.INACTIVE);
    }

    @Transactional
    public void updateName(Long id, String name) {
        Product product = findById(id);
        product.setName(name);
    }

    @Transactional
    public void updateDescription(Long id, String description) {
        Product product = findById(id);
        product.setDescription(description);
    }

    @Transactional
    public void updateAmount(Long id, BigDecimal amount) {
        Product product = findById(id);
        product.setAmount(amount);
    }

    @Transactional(readOnly = true)
    public Page<ProductProjection> findBestSelling(Pageable pageable) {
        return productRepository.findBestSelling(pageable);
    }

    @Transactional(readOnly = true)
    public Page<ProductProjection> findAll(Pageable pageable) {
        return productRepository.findAllPageable(pageable);
    }
}
