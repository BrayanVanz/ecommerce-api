package br.com.compass.ecommerce_api.services;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.compass.ecommerce_api.entities.CartItem;
import br.com.compass.ecommerce_api.entities.Product;
import br.com.compass.ecommerce_api.entities.Purchase;
import br.com.compass.ecommerce_api.entities.Stock;
import br.com.compass.ecommerce_api.entities.User;
import br.com.compass.ecommerce_api.exceptions.CartEmptyException;
import br.com.compass.ecommerce_api.exceptions.PurchasePeriodInvalidException;
import br.com.compass.ecommerce_api.projections.TopBuyerProjection;
import br.com.compass.ecommerce_api.repositories.PurchaseRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final UserService userService;
    private final CartItemService cartItemService;
    private final StockService stockService;

    @Transactional
    public BigDecimal getTotalAmount(String period) {
        LocalDateTime start;
        
        switch (period) {
            case "day":
                start = LocalDate.now().atStartOfDay();
                break;
            case "week":
                start = LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay();
                break;
            case "month":
                start = LocalDate.now().withDayOfMonth(1).atStartOfDay();
                break;
            default:
                throw new PurchasePeriodInvalidException("Invalid period. Use 'day', 'week', or 'month'");
        }

        return Optional
            .ofNullable(purchaseRepository.getTotalAmount(start, LocalDateTime.now()))
            .orElse(BigDecimal.ZERO);
    }

    @Transactional
    public Integer getTotalPurchases(String period) {
        LocalDateTime start;

        switch (period) {
            case "day":
                start = LocalDate.now().atStartOfDay();
                break;
            case "week":
                start = LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay();
                break;
            case "month":
                start = LocalDate.now().withDayOfMonth(1).atStartOfDay();
                break;
            default:
                throw new PurchasePeriodInvalidException("Invalid period. Use 'day', 'week', or 'month'");
        }

        return Optional
            .ofNullable(purchaseRepository.getTotalPurchases(start, LocalDateTime.now()))
            .orElse(0);
    }

    @Transactional
    public void performPurchase(Long id) {
        User user = userService.findById(id);
        List<CartItem> cart = user.getCart();

        if (cart.isEmpty()) {
            throw new CartEmptyException("Cart is currently empty");
        }
        
        for (CartItem item : cart) {
            Product product = item.getProduct();
            Stock stock = stockService.findByProductId(product.getId());
            product.setTimesPurchased(product.getTimesPurchased() + item.getQuantity());
            stockService.decrease(stock.getId(), item.getQuantity());
        }

        BigDecimal totalAmount = cart.stream()
            .map(item -> item.getProduct().getAmount().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Purchase purchase = new Purchase();
        purchase.setUser(user);
        purchase.setTotalAmount(totalAmount);

        purchaseRepository.save(purchase);

        cartItemService.clearCart(id);
    }

    @Transactional(readOnly = true)
    public Page<TopBuyerProjection> findTopBuyers(Pageable pageable) {
        return purchaseRepository.findTopBuyers(pageable);
    }
}
