package com.example.cdrservice.utils;

import com.example.cdrservice.entity.CDRRecord;
import com.example.cdrservice.entity.Customer;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Класс для генерации CDR-записей (Call Detail Records) для клиентов.
 * Генерирует случайные вызовы с указанием типа, времени начала и окончания.
 */
public class CallsGenerator {
    private static final Logger LOGGER = Logger.getLogger(CallsGenerator.class.getName());

    /**
     * Текущий год для генерации записей и генератор рандомных чисел
     */
    @Getter
    private static int year = 2023;
    private static final Random RANDOM = new Random();

    /**
     * Параметры Гауссовского (Нормального) распределения для генерации звонков.
     */
    private static final double AVERAGE_CALL_TIME_IN_SECONDS = 60;
    private static final long DISPERSION = 20;
    private static final long MAX_LENGTH_CALL_IN_SECONDS = TimeUnit.MINUTES.toSeconds(30);

    /**
     * Максимально возможное количество исходящих звонков, которое генерируем для одного пользователя за один год
     */
    private static final int UPPER_BOUND_QUANTITY_CALLS = 150;

    /**
     * Генерирует список CDR-записей для всех клиентов.
     *
     * @param customerList список клиентов
     * @return список CDR-записей
     */
    public static List<CDRRecord> makeCDRRecords(List<Customer> customerList) {
        if (customerList == null || customerList.isEmpty()) {
            LOGGER.warning("Customer list is empty or null.");
            return new ArrayList<>();
        }

        List<CDRRecord> CDRRecordList = new ArrayList<>();
        LOGGER.info("Starting CDR generation for " + customerList.size() + " customers.");

        try {
            for (Customer currentCustomer : customerList) {
                List<List<LocalDateTime>> currentCustomerCalls = generateCallsForCustomer(RANDOM.nextInt(UPPER_BOUND_QUANTITY_CALLS));
                LOGGER.info("Generated " + currentCustomerCalls.size() + " calls for customer: " + currentCustomer.getMsisdn());

                String callType;
                Customer receiverCustomer;
                for (List<LocalDateTime> call : currentCustomerCalls) {
                    callType = (RANDOM.nextInt(2) == 0) ? "01" : "02";
                    do {
                        receiverCustomer = customerList.get(RANDOM.nextInt(customerList.size()));
                    } while (receiverCustomer.getMsisdn() == currentCustomer.getMsisdn());

                    CDRRecord cdrCaller = new CDRRecord(
                            callType,
                            call.get(0),
                            call.get(1),
                            receiverCustomer,
                            currentCustomer,
                            receiverCustomer.getMsisdn(),
                            currentCustomer.getMsisdn()
                    );
                    CDRRecordList.add(cdrCaller);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error generating CDR records: " + e.getMessage(), e);
            throw new RuntimeException("Failed to generate CDR records", e);
        }
        year++;
        LOGGER.info("Current generation year: " + year);

        return CDRRecordList;
    }

    /**
     * Генерирует случайную дату в текущем году.
     *
     * @return случайная дата и время
     */
    private static LocalDateTime generateDate() {
        int month = RANDOM.nextInt(12) + 1;
        int day = RANDOM.nextInt(Month.of(month).length(year % 4 == 0 && (year % 100 != 0 || year % 400 == 0))) + 1;
        int hour = RANDOM.nextInt(24);
        int minute = RANDOM.nextInt(60);
        int second = RANDOM.nextInt(60);

        return LocalDateTime.of(year, month, day, hour, minute, second);
    }

    /**
     * Генерирует список вызовов для одного клиента.
     *
     * @param quantityCalls количество вызовов, которое хотим сгенерировать
     * @return список вызовов (каждый вызов — это список из двух LocalDateTime: начало и конец)
     */
    private static List<List<LocalDateTime>> generateCallsForCustomer(int quantityCalls) {
        if (quantityCalls <= 0) {
            LOGGER.warning("Quantity of calls is less than or equal to 0.");
            return new ArrayList<>();
        }
        List<List<LocalDateTime>> customerCalls = new ArrayList<>();

        try {
            double gaussianValue;
            for (int i = 0; i < quantityCalls; i++) {
                gaussianValue = Math.min(Math.abs(AVERAGE_CALL_TIME_IN_SECONDS + DISPERSION * RANDOM.nextGaussian()), MAX_LENGTH_CALL_IN_SECONDS);
                LocalDateTime startTime = CallsGenerator.generateDate();
                LocalDateTime endTime = startTime.plusSeconds((long) gaussianValue);
                customerCalls.add(List.of(startTime, endTime));
            }
            LOGGER.info("Generated " + customerCalls.size() + " calls for the customer.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error generating calls for customer: " + e.getMessage(), e);
            throw new RuntimeException("Failed to generate calls for customer", e);
        }

        return customerCalls;
    }
}