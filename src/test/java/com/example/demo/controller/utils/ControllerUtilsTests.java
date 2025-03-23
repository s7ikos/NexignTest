package com.example.demo.controller.utils;

import com.example.cdrservice.controller.utils.ControllerUtils;
import com.example.cdrservice.entity.CDRRecord;
import com.example.cdrservice.entity.Customer;
import com.example.cdrservice.utils.CallsGenerator;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ControllerUtilsTests {
    /**
     * 1. Проверка, что не вернется null
     * 2. Проверка, что строка не пустая
     */
    @Test
    public void testCalculateTotalTime() throws
            NoSuchMethodException,
            InvocationTargetException,
            IllegalAccessException {
        List<Customer> customerList = new ArrayList<>();
        Customer customer1 = new Customer("79115837280");
        Customer customer2 = new Customer("79115837281");
        Customer customer3 = new Customer("79115837282");
        Customer customer4 = new Customer("79115837283");
        Customer customer5 = new Customer("79115837284");
        Customer customer6 = new Customer("79115837285");
        customerList.add(customer1);
        customerList.add(customer2);
        customerList.add(customer3);
        customerList.add(customer4);
        customerList.add(customer5);
        customerList.add(customer6);

        List<CDRRecord> CDRRecords = CallsGenerator.makeCDRRecords(customerList);
        Class<ControllerUtils> controllerUtilsClass = ControllerUtils.class;
        Method calculateTotalTimeMethod = controllerUtilsClass.getDeclaredMethod("calculateTotalTime", List.class,
                LocalDateTime.class, LocalDateTime.class);
        calculateTotalTimeMethod.setAccessible(true);

        LocalDateTime start = LocalDateTime.of(2023, 2, 10, 10, 10);
        LocalDateTime end = LocalDateTime.of(2023, 10, 10, 10, 10);
        String time = (String) calculateTotalTimeMethod.invoke(null, CDRRecords, start, end);
        assertNotNull(time);
        assertNotEquals(time.length(), 0);
    }

    /**
     * Проверка, что сгенерированные uuid уникальны
     */
    @Test
    public void testGenerateUniqueUuid() throws
            NoSuchMethodException,
            InvocationTargetException,
            IllegalAccessException {
        long repeats = 10000000;
        Class<ControllerUtils> controllerUtilsClass = ControllerUtils.class;
        Method generateUniqueUuidMethod = controllerUtilsClass.getDeclaredMethod("generateUniqueUUID");
        generateUniqueUuidMethod.setAccessible(true);

        Set<String> uuidSet = new HashSet<>();
        for (int i = 0; i < repeats; i++) {
            uuidSet.add((String) generateUniqueUuidMethod.invoke(null));
        }

        assertEquals(uuidSet.size(), repeats);
    }
}
