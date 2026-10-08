package com.sahana.sahanamart.dao;

import com.sahana.sahanamart.model.User;
import com.sahana.sahanamart.util.DBUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DAO Integration test running against embedded in-memory H2 (jdbc:h2:mem:test).
 * Complies with Week 1 and Week 7 requirements.
 */
public class UserDAOTest {
    private static UserDAO userDAO;

    @BeforeAll
    public static void setupDatabase() throws Exception {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.h2.Driver");
        config.setJdbcUrl("jdbc:h2:mem:usertest;DB_CLOSE_DELAY=-1;MODE=MySQL");
        config.setUsername("sa");
        config.setPassword("");
        config.setMaximumPoolSize(5);

        HikariDataSource ds = new HikariDataSource(config);
        DBUtil.setDataSource(ds);

        try (Connection conn = ds.getConnection()) {
            DBUtil.executeSqlScript(conn, "db/schema.sql");
        }

        userDAO = new UserDAOImpl();
    }

    @AfterAll
    public static void tearDown() {
        DBUtil.closePool();
    }

    @Test
    public void testCreateAndFindUser() {
        User user = new User();
        user.setName("Test Buyer");
        user.setEmail("testbuyer@sahanamart.com");
        user.setPasswordHash("$2a$10$abcdefghijklmnopqrstuvwxyz1234567890");
        user.setRole("BUYER");
        user.setPhone("9876543210");
        user.setAddress("Test Address");

        User created = userDAO.create(user);
        assertNotNull(created.getId());

        Optional<User> found = userDAO.findById(created.getId());
        assertTrue(found.isPresent());
        assertEquals("testbuyer@sahanamart.com", found.get().getEmail());
        assertEquals("BUYER", found.get().getRole());
    }

    @Test
    public void testExistsByEmail() {
        User user = new User();
        user.setName("Check Exists");
        user.setEmail("check@sahanamart.com");
        user.setPasswordHash("hash123");
        user.setRole("SELLER");
        userDAO.create(user);

        assertTrue(userDAO.existsByEmail("check@sahanamart.com"));
        assertFalse(userDAO.existsByEmail("nonexistent@sahanamart.com"));
    }

    @Test
    public void testFindAll() {
        User user = new User();
        user.setName("Find All Test User");
        user.setEmail("findall_" + System.currentTimeMillis() + "@sahanamart.com");
        user.setPasswordHash("hash123");
        user.setRole("BUYER");
        userDAO.create(user);

        List<User> all = userDAO.findAll();
        assertNotNull(all);
        assertFalse(all.isEmpty());
    }
}
