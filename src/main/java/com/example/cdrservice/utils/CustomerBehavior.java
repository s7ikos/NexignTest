package com.example.cdrservice.utils;

import com.example.cdrservice.controller.SaveCDR;
import com.example.cdrservice.entity.CDRRecord;
import com.example.cdrservice.entity.Customer;
import com.example.cdrservice.repository.CDRRepository;
import com.example.cdrservice.repository.MsisdnRepository;
import lombok.AllArgsConstructor;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Класс, генерирующий CDR-записи и сохраняющий их в базу данных.
 */
@AllArgsConstructor
public class CustomerBehavior extends Thread {
    private final CDRRepository cdrRepository;
    private final MsisdnRepository msisdnRepository;
    private static final Logger LOGGER = Logger.getLogger(SaveCDR.class.getName());
    private static final long SERVER_SLEEP_TIME_SECONDS = TimeUnit.SECONDS.toMillis(60);

    /**
     * Основной метод потока, который выполняется при запуске.
     * Генерирует CDR-записи, сортирует их по времени начала и сохраняет в базу данных.
     * Цикл повторяется каждые 60 секунд.
     */
    @Override
    public void run() {
        while (true) {
            try {
                List<Customer> listCustomers = msisdnRepository.findAll();
                List<CDRRecord> CDRRecords = CallsGenerator.makeCDRRecords(listCustomers);
                CDRRecords.sort(Comparator.comparing(CDRRecord::getStartTime));
                cdrRepository.saveAll(CDRRecords);
                LOGGER.info("CDR records saved to the database.");
                Thread.sleep(SERVER_SLEEP_TIME_SECONDS);
            } catch (InterruptedException e) {
                LOGGER.log(Level.SEVERE, "Thread was interrupted: " + e.getMessage(), e);
                throw new RuntimeException("Thread interrupted", e);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "An error occurred: " + e.getMessage(), e);
            }
        }
    }
}