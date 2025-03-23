package com.example.cdrservice.service;

import com.example.cdrservice.entity.Customer;
import com.example.cdrservice.repository.CDRRepository;
import com.example.cdrservice.repository.MsisdnRepository;
import com.example.cdrservice.utils.CustomerBehavior;
import com.example.cdrservice.utils.CustomersGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Сервис для инициализации данных и запуска фоновых задач.
 */
@Service
public class CDRService {
    private static final Logger LOGGER = Logger.getLogger(CDRService.class.getName());

    /**
     * Количество клиентов, которое нужно сгенерировать.
     */
    private static final int CUSTOMERS_QUANTITY = 20;

    /**
     * Задержка перед запуском задачи (в миллисекундах).
     */
    private static final long INITIAL_DELAY = 1000;

    @Autowired
    private MsisdnRepository msisdnRepository;

    @Autowired
    private CDRRepository cdrRepository;

    /**
     * Инициализирует данные и запускает фоновую задачу для генерации CDR-записей.
     */
    @Scheduled(initialDelay = INITIAL_DELAY)
    public void init() {
        try {
            LOGGER.info("Starting initialization of customers and CDR generation...");

            List<Customer> customerList = CustomersGenerator.makeCustomers(CUSTOMERS_QUANTITY);
            msisdnRepository.saveAll(customerList);
            LOGGER.info("Generated and saved " + customerList.size() + " customers.");

            CustomerBehavior customerBehavior = new CustomerBehavior(cdrRepository, msisdnRepository);
            customerBehavior.setDaemon(true);
            customerBehavior.start();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during initialization: " + e.getMessage(), e);
            throw new RuntimeException("Failed to initialize CDRService", e);
        }
    }
}