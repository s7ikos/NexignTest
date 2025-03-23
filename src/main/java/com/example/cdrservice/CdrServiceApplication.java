package com.example.cdrservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import java.util.logging.Logger;

/**
 * Основной класс приложения CDR Service.
 * Этот класс запускает Spring Boot приложение и включает поддержку:
 * Планировщика задач ({@link EnableScheduling})
 * Асинхронных операций ({@link EnableAsync})
 */
@SpringBootApplication
@EnableScheduling
@EnableAsync
public class CdrServiceApplication {
    private static final Logger LOGGER = Logger.getLogger(CdrServiceApplication.class.getName());

    /**
     * Точка входа в приложение.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        LOGGER.info("Starting CDR Service application...");
        ConfigurableApplicationContext context = SpringApplication.run(CdrServiceApplication.class, args);

        Environment env = context.getEnvironment();
        String port = env.getProperty("server.port");
        String contextPath = env.getProperty("server.servlet.context-path", "/");

        LOGGER.info("CDR Service application started successfully on port " + port + " with context path '" + contextPath + "'.");
    }
}