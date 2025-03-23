package com.example.cdrservice.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
/**
 * Класс для запроса UDR-записи пользователя за определенный период
 */
public class DataToGetUDR {
    /**
     * Поля для хранения номера телефона, начала и конца запрашиваемого периода звонков
     */
    private String msisdn;
    private LocalDateTime startOfPeriod;
    private LocalDateTime endOfPeriod;

    public DataToGetUDR(LocalDateTime startOfPeriod, LocalDateTime endOfPeriod) {
        this.startOfPeriod = startOfPeriod;
        this.endOfPeriod = endOfPeriod;
    }
}
