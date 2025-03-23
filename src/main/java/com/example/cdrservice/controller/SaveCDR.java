package com.example.cdrservice.controller;

import com.example.cdrservice.controller.utils.ControllerUtils;
import com.example.cdrservice.entity.CDRRecord;
import com.example.cdrservice.entity.Customer;
import com.example.cdrservice.repository.MsisdnRepository;
import com.example.cdrservice.utils.DataToGetUDR;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Контроллер для сохранения CDR-записей в файл.
 */
@RestController
public class SaveCDR {
    private static final Logger LOGGER = Logger.getLogger(SaveCDR.class.getName());
    @Autowired
    private ControllerUtils controllerUtils;
    @Autowired
    private MsisdnRepository msisdnRepository;

    /**
     * Сохраняет CDR-записи для указанного абонента в файл.
     *
     * @param request запрос с данными абонента
     * @return ответ с результатом операции
     */
    @PostMapping("/save-cdr")
    public ResponseEntity<String> saveCDR(@RequestBody DataToGetUDR request) {
        String uuid = ControllerUtils.generateUniqueUUID();
        if (request.getStartOfPeriod() == null || request.getEndOfPeriod() == null) {
            return ResponseEntity.badRequest().body("Error with period borders\nUUID: " + uuid);
        }
        try {
            Optional<Customer> customerOptional = msisdnRepository.findByMsisdn(request.getMsisdn());
            if (customerOptional.isEmpty()) {
                LOGGER.warning("Customer not found for MSISDN: " + request.getMsisdn());
                return ResponseEntity.badRequest().body("Customer not found for MSISDN: " + request.getMsisdn() + "\nUUID: " + uuid);
            }
            Customer customer = customerOptional.get();
            List<CDRRecord> allCalls = new ArrayList<>();
            allCalls.addAll(customer.getIncomingCalls());
            allCalls.addAll(customer.getReceivedCalls());
            allCalls.sort(Comparator.comparing(CDRRecord::getStartTime));
            controllerUtils.saveCDRFile(allCalls, request.getMsisdn(), uuid);

            return ResponseEntity.ok().body("CDR File saved for: " + request.getMsisdn() + "\nUUID: " + uuid);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error saving CDR file for MSISDN: " + request.getMsisdn(), e);
            return ResponseEntity.internalServerError().body("Failed to save CDR file " + "\n UUID: " + uuid + "\n" + e.getMessage());
        }
    }
}