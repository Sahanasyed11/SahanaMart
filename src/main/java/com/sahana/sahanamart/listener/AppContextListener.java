package com.sahana.sahanamart.listener;

import com.sahana.sahanamart.util.DBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Application Lifecycle Listener owning the single HikariCP Connection Pool.
 * Complies with Section 1: Single ServletContextListener owning HikariCP lifecycle.
 */
@WebListener
public class AppContextListener implements ServletContextListener {
    private static final Logger logger = LoggerFactory.getLogger(AppContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("==================================================================");
        logger.info("Starting SahanaMart Application - Anna University Capstone");
        logger.info("Initializing HikariCP Database Connection Pool & Running DDL...");
        logger.info("==================================================================");

        try {
            DBUtil.initialize();
            sce.getServletContext().setAttribute("dbReady", true);
            logger.info("Database and HikariCP connection pool ready.");
        } catch (Exception e) {
            logger.error("FATAL: Failed to initialize application database pool: {}", e.getMessage(), e);
            sce.getServletContext().setAttribute("dbReady", false);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("Shutting down SahanaMart Application...");
        DBUtil.closePool();
        logger.info("HikariCP Connection Pool closed successfully.");
    }
}
