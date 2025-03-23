package com.example.cdrservice.controller.utils;

import com.example.cdrservice.entity.CDRRecord;
import com.example.cdrservice.entity.Customer;
import com.example.cdrservice.repository.MsisdnRepository;
import com.example.cdrservice.utils.DataToGetUDR;
import com.example.cdrservice.utils.TotalTimeObject;
import com.example.cdrservice.utils.UDRObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Утилитный класс для обработки запросов, связанных с CDR-записями.
 * Предоставляет методы для генерации UDR-отчетов и сохранения CDR-записей в файл.
 */
@Component
public class ControllerUtils {
    private static final Logger LOGGER = Logger.getLogger(ControllerUtils.class.getName());
    @Autowired
    private MsisdnRepository msisdnRepository;

    private final Path ROOT_PATH = Paths.get(System.getProperty("user.dir") + "/reports").toAbsolutePath();

    /**
     * Генерирует UDR-отчет для указанного абонента.
     *
     * @param request запрос с данными абонента
     * @return UDR-отчет
     * @throws RuntimeException если абонент не найден
     */
    public UDRObject getUDRForCustomer(DataToGetUDR request) throws RuntimeException {
        String msisdn = request.getMsisdn();
        LocalDateTime startTime = request.getStartOfPeriod();
        LocalDateTime endTime = request.getEndOfPeriod();
        Optional<Customer> customerOptional = msisdnRepository.findByMsisdn(msisdn);

        if (customerOptional.isEmpty()) {
            LOGGER.warning("Customer not found for MSISDN: " + msisdn);
            throw new RuntimeException("Customer not found");
        }
        Customer customer = customerOptional.get();

        LOGGER.info("Generating UDR report for customer: " + msisdn);
        return getUDRCustomer(customer, startTime, endTime);
    }

    /**
     * Генерирует UDR-отчеты для всех абонентов.
     *
     * @param request запрос с данными периода
     * @return список UDR-отчетов
     */
    public List<UDRObject> getUDRForAllCustomers(DataToGetUDR request) {
        List<CompletableFuture<UDRObject>> listUdrFutures = new ArrayList<>();
        List<Customer> customers = msisdnRepository.findAll();

        for (Customer customer : customers) {
            listUdrFutures.add(CompletableFuture.completedFuture(getUDRCustomer(customer, request.getStartOfPeriod(), request.getEndOfPeriod())));
        }
        CompletableFuture.allOf(listUdrFutures.toArray(new CompletableFuture[0])).join();

        return listUdrFutures.stream()
                .map(CompletableFuture::join)
                .toList();
    }

    /**
     * Генерирует UDR-отчет для конкретного абонента.
     *
     * @param customer  абонент
     * @param startTime начало периода
     * @param endTime   конец периода
     * @return UDR-отчет
     */
    private static UDRObject getUDRCustomer(Customer customer, LocalDateTime startTime, LocalDateTime endTime) {
        TotalTimeObject incomingCalls = new TotalTimeObject(calculateTotalTime(customer.getIncomingCalls(), startTime, endTime));
        TotalTimeObject outcomingCalls = new TotalTimeObject(calculateTotalTime(customer.getReceivedCalls(), startTime, endTime));
        return new UDRObject(customer.getMsisdn(), incomingCalls, outcomingCalls);
    }

    /**
     * Вычисляет общую длительность звонков в указанном периоде.
     *
     * @param calls     список звонков
     * @param startTime начало периода
     * @param endTime   конец периода
     * @return общая длительность в формате "HH:MM:SS"
     */
    private static String calculateTotalTime(List<CDRRecord> calls, LocalDateTime startTime, LocalDateTime endTime) {
        if (endTime.isBefore(startTime)) {
            throw new RuntimeException("startTime can't be after endTime.");
        }
        calls = calls.stream()
                .filter(call -> call.getStartTime().isAfter(startTime) && call.getEndTime().isBefore(endTime))
                .toList();
        //
        int totalSeconds = calls.stream()
                .mapToInt(call -> (int) Duration.between(call.getStartTime(), call.getEndTime()).getSeconds())
                .sum();

        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    /**
     * Генерирует уникальный UUID.
     *
     * @return уникальный UUID
     */
    public static String generateUniqueUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * Сохраняет CDR-записи в файл.
     *
     * @param CDRRecordList список CDR-записей
     * @param msisdn        номер телефона абонента
     * @throws RuntimeException если произошла ошибка при сохранении файла
     */
    public void saveCDRFile(List<CDRRecord> CDRRecordList, String msisdn, String uuid) {
        Path filePath = Paths.get(ROOT_PATH + "/" + msisdn + "_" + uuid + ".txt");

        try {
            if (!Files.exists(ROOT_PATH.toAbsolutePath())) {
                Files.createDirectory(ROOT_PATH.toAbsolutePath());
            }
            Files.deleteIfExists(filePath);
            Path file = Files.createFile(filePath);

            try (FileOutputStream outputStream = new FileOutputStream(file.toFile())) {
                for (CDRRecord cdrRecord : CDRRecordList) {
                    outputStream.write((cdrRecord.makeCDRString() + "\n").getBytes());
                    outputStream.flush();
                }
            }
            LOGGER.info("CDR file saved successfully for MSISDN: " + msisdn);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to save CDR file for MSISDN: " + msisdn, e);
            throw new RuntimeException("Failed to save CDR file", e);
        }
    }
}