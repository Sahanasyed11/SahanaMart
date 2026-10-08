package com.sahana.sahanamart;

import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Standalone Embedded Tomcat Launcher for SahanaMart.
 * Enables zero-setup local execution: mvn compile exec:java
 */
public class AppLauncher {
    private static final Logger logger = LoggerFactory.getLogger(AppLauncher.class);

    public static void main(String[] args) throws Exception {
        int port = 8080;
        String portStr = System.getenv("PORT");
        if (portStr != null && !portStr.trim().isEmpty()) {
            port = Integer.parseInt(portStr.trim());
        }

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        tomcat.getConnector(); // Trigger connector initialization

        String webappDirLocation = "src/main/webapp/";
        File webappDir = new File(webappDirLocation);
        if (!webappDir.exists()) {
            webappDir = new File(".");
        }

        StandardContext ctx = (StandardContext) tomcat.addWebapp("", webappDir.getAbsolutePath());
        ctx.setParentClassLoader(AppLauncher.class.getClassLoader());

        // Declare alternative location for classes to allow IDE and Maven hot execution
        File additionWebInfClasses = new File("target/classes");
        if (additionWebInfClasses.exists()) {
            WebResourceRoot resources = new StandardRoot(ctx);
            resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes",
                    additionWebInfClasses.getAbsolutePath(), "/"));
            ctx.setResources(resources);
        }

        logger.info("==================================================================");
        logger.info("  🛒 SahanaMart Web Application Server Started!");
        logger.info("  URL: http://localhost:{}", port);
        logger.info("  Health Check: http://localhost:{}/api/v1/health", port);
        logger.info("==================================================================");

        tomcat.start();
        tomcat.getServer().await();
    }
}
