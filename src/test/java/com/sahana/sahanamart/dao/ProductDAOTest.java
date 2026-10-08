package com.sahana.sahanamart.dao;

import com.sahana.sahanamart.model.Product;
import com.sahana.sahanamart.model.User;
import com.sahana.sahanamart.util.DBUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ProductDAOTest {
    private static ProductDAO productDAO;
    private static UserDAO userDAO;
    private static Long sellerId;

    @BeforeAll
    public static void setup() throws Exception {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.h2.Driver");
        config.setJdbcUrl("jdbc:h2:mem:prodtest;DB_CLOSE_DELAY=-1;MODE=MySQL");
        config.setUsername("sa");
        config.setPassword("");
        config.setMaximumPoolSize(5);

        HikariDataSource ds = new HikariDataSource(config);
        DBUtil.setDataSource(ds);

        try (Connection conn = ds.getConnection()) {
            DBUtil.executeSqlScript(conn, "db/schema.sql");
        }

        userDAO = new UserDAOImpl();
        productDAO = new ProductDAOImpl();

        User seller = new User();
        seller.setName("Seller One");
        seller.setEmail("sellerone@sahanamart.com");
        seller.setPasswordHash("hash");
        seller.setRole("SELLER");
        User createdSeller = userDAO.create(seller);
        sellerId = createdSeller.getId();
    }

    @AfterAll
    public static void tearDown() {
        DBUtil.closePool();
    }

    @Test
    public void testCreateAndFindProduct() {
        Product p = new Product();
        p.setSellerId(sellerId);
        p.setName("Wireless Mouse");
        p.setDescription("Ergonomic 2.4GHz optical mouse");
        p.setPrice(new BigDecimal("499.00"));
        p.setStockQty(20);
        p.setCategory("Electronics");
        p.setImageUrl("https://example.com/mouse.jpg");

        Product created = productDAO.create(p);
        assertNotNull(created.getId());

        Optional<Product> found = productDAO.findById(created.getId());
        assertTrue(found.isPresent());
        assertEquals("Wireless Mouse", found.get().getName());
        assertEquals(20, found.get().getStockQty());
    }

    @Test
    public void testSearchAndFilter() {
        Product p = new Product();
        p.setSellerId(sellerId);
        p.setName("Java Programming Textbook");
        p.setDescription("Complete guide to Java 17 and servlets");
        p.setPrice(new BigDecimal("750.00"));
        p.setStockQty(10);
        p.setCategory("Books");
        productDAO.create(p);

        List<Product> results = productDAO.search("Java", "Books", 10, 0);
        assertFalse(results.isEmpty());
        assertEquals("Java Programming Textbook", results.get(0).getName());
    }

    @Test
    public void testStockDeduction() throws Exception {
        Product p = new Product();
        p.setSellerId(sellerId);
        p.setName("USB Flash Drive 64GB");
        p.setDescription("High speed USB 3.0");
        p.setPrice(new BigDecimal("350.00"));
        p.setStockQty(10);
        p.setCategory("Electronics");
        Product created = productDAO.create(p);

        try (Connection conn = DBUtil.getConnection()) {
            boolean deducted = productDAO.deductStock(created.getId(), 3, conn);
            assertTrue(deducted);
        }

        Product updated = productDAO.findById(created.getId()).orElseThrow();
        assertEquals(7, updated.getStockQty());
    }
}
