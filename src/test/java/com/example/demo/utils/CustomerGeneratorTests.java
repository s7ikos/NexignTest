package com.example.demo.utils;

import com.example.cdrservice.entity.Customer;
import com.example.cdrservice.utils.CustomersGenerator;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerGeneratorTests {
    /**
     * 1. Проверка, что не вернется null
     * 2. Проверка, что не пустой
     * 3. ПроверкаЮ что сделает столько пользователей, сколько указано
     */
    @Test
    public void testMakeCustomers() {
        int customersQuantity = 100000;
        List<Customer> customerList = CustomersGenerator.makeCustomers(customersQuantity);
        assertNotNull(customerList);
        assertFalse(customerList.isEmpty());
        assertEquals(customersQuantity, customerList.size());
        Set<String> phoneNumbersSet = new HashSet<>();

        for (Customer customer : customerList) {
            phoneNumbersSet.add(customer.getMsisdn());
        }
        assertEquals(phoneNumbersSet.size(), customersQuantity);
    }

    /**
     * Проверка, что все  номера будут начинаться с PHONE_NUMBER_PREFIX
     */
    @Test
    public void testGeneratePhoneNumber() throws
            NoSuchMethodException,
            InvocationTargetException,
            IllegalAccessException, NoSuchFieldException {
        int customersQuantity = 1000000;
        Class<CustomersGenerator> customerGeneratorClass = CustomersGenerator.class;
        Method generateNumbersForCustomerMethod = customerGeneratorClass.getDeclaredMethod("generatePhoneNumber");
        generateNumbersForCustomerMethod.setAccessible(true);

        Field prefixField = customerGeneratorClass.getDeclaredField("PHONE_NUMBER_PREFIX");
        prefixField.setAccessible(true);
        String PHONE_NUMBER_PREFIX = (String) prefixField.get(null);

        for (int i = 0; i < customersQuantity; i++) {
            String phoneNumber = (String) generateNumbersForCustomerMethod.invoke(null);
            assertTrue(phoneNumber.contains(PHONE_NUMBER_PREFIX));
        }
    }
}
