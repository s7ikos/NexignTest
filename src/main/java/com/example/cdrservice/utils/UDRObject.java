package com.example.cdrservice.utils;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor

/**
 * Класс для получения UDR-записи
 */
public class UDRObject {
    /**
     * Номер телефона запрошенного пользователя
     * общее время входящих incomingCall звонков и исходящих outcomingCall звонков
     */
    private String msisdn;
    private TotalTimeObject incomingCall;
    private TotalTimeObject outcomingCall;
}
