package com.example.cdrservice.utils;

import com.example.cdrservice.entity.Customer;

import java.util.*;

/**
 * Класс, генерирующий пользователей с рандомным номером телефона.
 */
public class CustomersGenerator {
    /**
     * PHONE_NUMBER_PREFIX
     * PHONE_NUMBER_SUFFIX
     * для генерации префикса и суффикса номера телефона
     */
    private static final String PHONE_NUMBER_PREFIX = "7911";
    private static final int PHONE_NUMBER_SUFFIX = 1_000_000;

    /**
     * создаем генератор случайных чисел для генерации номера
     */
    private static final Random random = new Random();

    /**
     * Метод генерирует список клиентов.
     *
     * @param numberOfCustomers количество клиентов для генерации.
     * @return список клиентов List<Customer>.
     */
    public static List<Customer> makeCustomers(int numberOfCustomers) {
        List<Customer> customersList = new ArrayList<>();
        Set<String> phoneNumbers = new HashSet<>();
        /**
         * Данный номер телефона добавляется всегда для удобства проверки
         */
        customersList.add(new Customer("79110000000"));

        for (int i = 1; i < numberOfCustomers; i++) {
            String phoneNumber = generatePhoneNumber();
            while (phoneNumbers.contains(phoneNumber)) {
                phoneNumber = generatePhoneNumber();
            }
            phoneNumbers.add(phoneNumber);
            customersList.add(new Customer(phoneNumber));
        }

        return customersList;
    }

    /**
     * Генерирует случайный номер телефона.
     *
     * @return случайный номер телефона.
     */
    private static String generatePhoneNumber() {
        int randomSuffix = random.nextInt(9 * PHONE_NUMBER_SUFFIX) + PHONE_NUMBER_SUFFIX;
        return PHONE_NUMBER_PREFIX + randomSuffix;
    }
}