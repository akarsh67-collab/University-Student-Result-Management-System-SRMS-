package com.university.srms.util;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Application lifecycle listener.
 * Initializes DB pool on startup, closes it on shutdown.
 */
@WebListener
public class AppInitializer implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(AppInitializer.class);

    @Override
    public void contextInitialized(ServletContextEvent event) {
        logger.info("University SRMS starting up...");
        try {
            DBConnection.init();
            logger.info("Application initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize application", e);
            throw new RuntimeException("App startup failed", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        logger.info("University SRMS shutting down...");
        DBConnection.shutdown();
    }
}
