package com.example.cdrservice.controller;

import com.example.cdrservice.controller.utils.ControllerUtils;
import com.example.cdrservice.utils.DataToGetUDR;
import com.example.cdrservice.utils.UDRObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Контроллер для генерации UDR-отчетов.
 * Предоставляет эндпоинты для создания отчетов по одному абоненту или всем абонентам.
 */
@RestController
public class GenerateUDR {
    private static final Logger LOGGER = Logger.getLogger(GenerateUDR.class.getName());
    private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    @Autowired
    private ControllerUtils controllerUtils;

    /**
     * Генерирует UDR-отчет для одного абонента.
     *
     * @param request запрос с данными абонента и периодом
     * @return JSON-ответ с UDR-отчетом
     */
    @PostMapping("/generate-udr")
    public ResponseEntity<String> getUDR(@RequestBody DataToGetUDR request) {
        if (request.getMsisdn() == null || request.getMsisdn().isEmpty() || request.getEndOfPeriod() == null
                || request.getStartOfPeriod() == null) {
            return ResponseEntity.badRequest().body("No msisdn or error with period borders");
        }
        try {
            UDRObject udrForCustomer = controllerUtils.getUDRForCustomer(request);
            String json = mapper.writeValueAsString(udrForCustomer);
            LOGGER.log(Level.INFO, "UDR file was generated successfully for MSISDN: " + request.getMsisdn());
            return ResponseEntity.ok().body(json);
        } catch (RuntimeException | JsonProcessingException e) {
            LOGGER.log(Level.SEVERE, "An error occurred during making UDR for MSISDN: " + request.getMsisdn(), e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    /**
     * Генерирует UDR-отчеты для всех абонентов.
     *
     * @param request запрос с данными периода
     * @return JSON-ответ со списком UDR-отчетов
     */
    @PostMapping("/generate-udr/all")
    public ResponseEntity<String> getAllUDR(@RequestBody DataToGetUDR request) {
        if (request.getMsisdn() != null){
            return ResponseEntity.badRequest().body("There must not be msisdn");
        }
        if (request.getEndOfPeriod() == null || request.getStartOfPeriod() == null) {
            return ResponseEntity.badRequest().body("Error with period borders");
        }
        try {
            List<UDRObject> udrForAllCustomers = controllerUtils.getUDRForAllCustomers(request);
            String json = mapper.writeValueAsString(udrForAllCustomers);
            LOGGER.log(Level.INFO, "UDR files were generated successfully for all customers.");
            return ResponseEntity.ok().body(json);
        } catch (RuntimeException | JsonProcessingException e) {
            LOGGER.log(Level.SEVERE, "An error occurred during making UDR for all customers.", e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}