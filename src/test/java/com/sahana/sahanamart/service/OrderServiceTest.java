package com.sahana.sahanamart.service;

import com.sahana.sahanamart.dao.CartDAO;
import com.sahana.sahanamart.dao.OrderDAO;
import com.sahana.sahanamart.dao.ProductDAO;
import com.sahana.sahanamart.dto.OrderRequestDTO;
import com.sahana.sahanamart.exception.ValidationException;
import com.sahana.sahanamart.model.CartItem;
import com.sahana.sahanamart.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderDAO orderDAO;

    @Mock
    private CartDAO cartDAO;

    @Mock
    private ProductDAO productDAO;

    private OrderService orderService;

    @BeforeEach
    public void setup() {
        orderService = new OrderService(orderDAO, cartDAO, productDAO);
    }

    @Test
    public void testCheckoutEmptyCartThrows() {
        when(cartDAO.findByUserId(1L)).thenReturn(Collections.emptyList());

        OrderRequestDTO dto = new OrderRequestDTO("123 Anna Salai, Chennai, TN - 600002", "UPI");
        assertThrows(ValidationException.class, () -> orderService.checkout(1L, dto));
    }

    @Test
    public void testCheckoutInsufficientStockThrows() {
        CartItem item = new CartItem();
        item.setProductId(100L);
        item.setQuantity(5);

        Product product = new Product();
        product.setId(100L);
        product.setName("Limited Edition Watch");
        product.setPrice(new BigDecimal("1500.00"));
        product.setStockQty(2); // Only 2 in stock, but user requested 5!
        item.setProduct(product);

        when(cartDAO.findByUserId(1L)).thenReturn(Collections.singletonList(item));
        when(productDAO.findById(100L)).thenReturn(Optional.of(product));

        OrderRequestDTO dto = new OrderRequestDTO("123 Anna Salai, Chennai, TN - 600002", "UPI");
        assertThrows(ValidationException.class, () -> orderService.checkout(1L, dto));
    }
}
