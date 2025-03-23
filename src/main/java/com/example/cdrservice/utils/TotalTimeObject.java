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
 * Класс для хранения общего времени звонков в UDR-записи за запрошенный период
 */
public class TotalTimeObject {
    private String totalTime;
}
