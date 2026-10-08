package com.sahana.sahanamart.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Central Database connection management using HikariCP.
 * Complies with Section 1: Single HikariCP lifecycle owned by ServletContextListener;
 * No direct DriverManager.getConnection() calls in business/DAO code.
 */
public class DBUtil {
    private static final Logger logger = LoggerFactory.getLogger(DBUtil.class);
    private static HikariDataSource dataSource;

    private DBUtil() {
    }

    public static synchronized void initialize() {
        if (dataSource != null && !dataSource.isClosed()) {
            return;
        }

        try {
            String driver = ConfigUtil.get("db.driver", "org.h2.Driver");
            String url = ConfigUtil.get("db.url", "jdbc:h2:file:./data/sahanamart;DB_CLOSE_DELAY=-1;AUTO_SERVER=TRUE;MODE=MySQL");
            String username = ConfigUtil.get("db.username", "sa");
            String password = ConfigUtil.get("db.password", "");

            HikariConfig config = new HikariConfig();
            config.setDriverClassName(driver);
            config.setJdbcUrl(url);
            config.setUsername(username);
            config.setPassword(password);

            config.setMaximumPoolSize(ConfigUtil.getInt("db.pool.maximumPoolSize", 10));
            config.setMinimumIdle(ConfigUtil.getInt("db.pool.minimumIdle", 2));
            config.setIdleTimeout(ConfigUtil.getInt("db.pool.idleTimeout", 30000));
            config.setConnectionTimeout(ConfigUtil.getInt("db.pool.connectionTimeout", 20000));
            config.setPoolName("SahanaMartHikariPool");

            dataSource = new HikariDataSource(config);
            logger.info("HikariCP connection pool initialized successfully: {}", url);

            // Run database schema, seed, and migrations on first startup
            runDatabaseInitScripts();

        } catch (Exception e) {
            logger.error("Failed to initialize HikariCP DataSource: {}", e.getMessage(), e);
            throw new RuntimeException("Database pool initialization failed", e);
        }
    }

    public static synchronized void setDataSource(HikariDataSource customDataSource) {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
        dataSource = customDataSource;
    }

    public static DataSource getDataSource() {
        if (dataSource == null || dataSource.isClosed()) {
            initialize();
        }
        return dataSource;
    }

    public static Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    public static synchronized void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            logger.info("Shutting down HikariCP connection pool...");
            dataSource.close();
            dataSource = null;
        }
    }

    public static boolean isHealthy() {
        if (dataSource == null || dataSource.isClosed()) {
            return false;
        }
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT 1");
             ResultSet rs = ps.executeQuery()) {
            return rs.next();
        } catch (Exception e) {
            logger.warn("Database health check failed: {}", e.getMessage());
            return false;
        }
    }

    private static void runDatabaseInitScripts() {
        try (Connection conn = getConnection()) {
            // Check if users table already exists
            boolean tablesExist = false;
            // Always execute schema.sql (all tables use CREATE TABLE IF NOT EXISTS)
            executeSqlScript(conn, "db/schema.sql");

            // Check if users table is populated; if not, run seed.sql
            boolean hasData = false;
            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM users");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    hasData = true;
                }
            } catch (Exception ignored) {
            }

            if (!hasData) {
                logger.info("Executing seed.sql to populate initial products and users...");
                executeSqlScript(conn, "db/seed.sql");
            }

            // Always run migrations
            executeSqlScript(conn, "db/migrations/V2__order_status.sql");

            // Ensure seeded admin/seller/buyer accounts have known valid bcrypt hashes
            ensureSeedAccounts(conn);

        } catch (Exception e) {
            logger.error("Error running database initialization scripts: {}", e.getMessage(), e);
        }
    }

    public static void executeSqlScript(Connection conn, String resourcePath) {
        try (InputStream is = DBUtil.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                logger.warn("SQL resource not found: {}", resourcePath);
                return;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                 Statement stmt = conn.createStatement()) {

                StringBuilder currentStatement = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("--") || line.startsWith("//")) {
                        continue;
                    }
                    currentStatement.append(line).append(" ");
                    if (line.endsWith(";")) {
                        String sql = currentStatement.toString().trim();
                        if (sql.endsWith(";")) {
                            sql = sql.substring(0, sql.length() - 1);
                        }
                        if (!sql.isEmpty()) {
                            try {
                                stmt.execute(sql);
                            } catch (SQLException ex) {
                                // Ignore duplicate column / duplicate table warnings during idempotent runs
                                logger.debug("Non-fatal SQL statement warning: {}", ex.getMessage());
                            }
                        }
                        currentStatement.setLength(0);
                    }
                }
            }
            logger.info("Successfully executed SQL script: {}", resourcePath);
        } catch (Exception e) {
            logger.warn("Notice executing SQL script {}: {}", resourcePath, e.getMessage());
        }
    }

    private static void ensureSeedAccounts(Connection conn) {
        String[][] seeds = {
                {"System Administrator", "admin@sahanamart.com", "Admin@123", "ADMIN"},
                {"Priya Sharma (Seller)", "seller@sahanamart.com", "Seller@123", "SELLER"},
                {"Arun Kumar (Buyer)", "buyer@sahanamart.com", "Buyer@123", "BUYER"}
        };

        for (String[] seed : seeds) {
            String name = seed[0];
            String email = seed[1];
            String plainPass = seed[2];
            String role = seed[3];

            try (PreparedStatement checkPs = conn.prepareStatement("SELECT id FROM users WHERE email = ?")) {
                checkPs.setString(1, email);
                try (ResultSet rs = checkPs.executeQuery()) {
                    String hash = PasswordUtil.hashPassword(plainPass);
                    if (rs.next()) {
                        long id = rs.getLong("id");
                        try (PreparedStatement updatePs = conn.prepareStatement("UPDATE users SET password_hash = ? WHERE id = ?")) {
                            updatePs.setString(1, hash);
                            updatePs.setLong(2, id);
                            updatePs.executeUpdate();
                        }
                    } else {
                        try (PreparedStatement insertPs = conn.prepareStatement(
                                "INSERT INTO users (name, email, password_hash, role, phone, address) VALUES (?, ?, ?, ?, ?, ?)")) {
                            insertPs.setString(1, name);
                            insertPs.setString(2, email);
                            insertPs.setString(3, hash);
                            insertPs.setString(4, role);
                            insertPs.setString(5, "9876543210");
                            insertPs.setString(6, "Chennai, Tamil Nadu");
                            insertPs.executeUpdate();
                        }
                    }
                }
            } catch (SQLException e) {
                logger.warn("Could not ensure seed account {}: {}", email, e.getMessage());
            }
        }
    }
}
