package com.example.demo.utils;

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

public class CallsGeneratorTests {

    /**
     * Проверка, что не вернется null
     */
    @Test
    public void testDateGeneration() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<CallsGenerator> callsGeneratorClass = CallsGenerator.class;
        Method generateDateMethod = callsGeneratorClass.getDeclaredMethod("generateDate");
        generateDateMethod.setAccessible(true);

        LocalDateTime result = (LocalDateTime) generateDateMethod.invoke(null);
        assertNotNull(result);
    }

    /**
     * 1. Проверка, что не вернется null
     * 2. Проверка, что не вернутся пустой список
     * 3. Проверка, что сделает столько звонков, сколько затребовано
     * 4. Проверка, что в каждом звонке будет два времени
     * 5. Проверка, что оба времени будут не null
     */
    @Test
    public void testGenerateCallsForCustomer() throws
            NoSuchMethodException,
            InvocationTargetException,
            IllegalAccessException {
        Class<CallsGenerator> callsGeneratorClass = CallsGenerator.class;
        Method generateCallsForCustomerMethod = callsGeneratorClass.getDeclaredMethod("generateCallsForCustomer", int.class);
        generateCallsForCustomerMethod.setAccessible(true);
        int callsQuantity = 10;
        List<List<LocalDateTime>> calls = (List<List<LocalDateTime>>) generateCallsForCustomerMethod.invoke(null, callsQuantity);
        assertNotNull(calls);
        assertFalse(calls.isEmpty());
        assertEquals(calls.size(), callsQuantity);
        for (List<LocalDateTime> call : calls) {
            assertEquals(2, call.size());
            assertNotNull(call.get(0));
            assertNotNull(call.get(1));
        }
    }

    /**
     * 1. Проверка, что не вернется null
     * 2. Проверка, что для каждого пользователя будет звонок
     */
    @Test
    public void testMakeCDRRecords() {
        List<Customer> customerList = new ArrayList<>();
        Customer customer1 = new Customer("79115837280");
        Customer customer2 = new Customer("79115837281");
        Customer customer3 = new Customer("79115837282");
        Customer customer4 = new Customer("79115837283");
        Customer customer5 = new Customer("79115837284");
        Customer customer6 = new Customer("79115837285");
        Customer customer7 = new Customer("79115837286");
        Customer customer8 = new Customer("79115837287");
        Customer customer9 = new Customer("79115837288");
        Customer customer10 = new Customer("79115837289");
        Customer customer11 = new Customer("79115837210");
        Customer customer12 = new Customer("791158372811");
        customerList.add(customer1);
        customerList.add(customer2);
        customerList.add(customer3);
        customerList.add(customer4);
        customerList.add(customer5);
        customerList.add(customer6);
        customerList.add(customer7);
        customerList.add(customer8);
        customerList.add(customer9);
        customerList.add(customer10);
        customerList.add(customer11);
        customerList.add(customer12);

        List<CDRRecord> CDRRecords = CallsGenerator.makeCDRRecords(customerList);
        assertNotNull(CDRRecords);

        Set<Customer> customerSetFromCdrRecords = new HashSet<>();
        for (CDRRecord cdrRecord : CDRRecords) {
            customerSetFromCdrRecords.add(cdrRecord.getCallerCustomer());
            customerSetFromCdrRecords.add(cdrRecord.getReceiverCustomer());
        }
        assertEquals(customerSetFromCdrRecords.size(), customerList.size());
    }
}
