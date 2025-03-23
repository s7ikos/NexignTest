package com.example.cdrservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Сущность, представляющая запись CDR (Call Detail Record).
 * Содержит информацию о звонке, включая тип, время начала и окончания, длительность, номера абонентов и связанных клиентов.
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "cdr_record")
public class CDRRecord {

    /**
     * Уникальный идентификатор записи CDR.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    /**
     * Тип звонка (например, входящий или исходящий).
     */
    @Column(name = "call_type", nullable = false)
    private String callType;

    /**
     * Время начала звонка.
     */
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    /**
     * Время окончания звонка.
     */
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    /**
     * Номер телефона звонящего.
     */
    @Column(name = "caller_number", nullable = false)
    private String callerNumber;

    /**
     * Номер телефона принимающего звонок.
     */
    @Column(name = "receiver_number", nullable = false)
    private String receiverNumber;

    /**
     * Идентификатор звонящего клиента (только для чтения).
     */
    @Column(name = "caller_customer", insertable = false, updatable = false)
    private Long callerId;

    /**
     * Звонящий клиент.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "caller_customer")
    private Customer callerCustomer;

    /**
     * Идентификатор принимающего звонок клиента (только для чтения).
     */
    @Column(name = "receiver_customer", insertable = false, updatable = false)
    private Long receiverId;

    /**
     * Принимающий звонок клиент.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "receiver_customer")
    private Customer receiverCustomer;

    /**
     * Конструктор для создания записи CDR.
     *
     * @param callType         тип звонка
     * @param startTime        время начала звонка
     * @param endTime          время окончания звонка
     * @param receiverCustomer принимающий звонок клиент
     * @param callerCustomer   звонящий клиент
     * @param receiverNumber   номер принимающего звонок
     * @param callerNumber     номер звонящего
     */
    public CDRRecord(String callType, LocalDateTime startTime, LocalDateTime endTime,
                     Customer receiverCustomer, Customer callerCustomer,
                     String receiverNumber, String callerNumber) {
        this.callType = callType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.callerId = callerCustomer != null ? callerCustomer.getId() : null;
        this.receiverId = receiverCustomer != null ? receiverCustomer.getId() : null;
        this.callerNumber = callerNumber;
        this.receiverNumber = receiverNumber;
        this.callerCustomer = callerCustomer;
        this.receiverCustomer = receiverCustomer;
    }

    /**
     * Преобразует запись CDR в строку в формате CSV.
     *
     * @return строка в формате: "callType,callerNumber,receiverNumber,startTime,endTime"
     */
    public String makeCDRString() {
        StringBuilder sb = new StringBuilder();
        sb.append(callType).append(',');
        sb.append(callerCustomer.getMsisdn()).append(',');
        sb.append(receiverCustomer.getMsisdn()).append(',');
        sb.append(startTime).append(',');
        sb.append(endTime);
        return sb.toString();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CDRRecord{");
        sb.append("id=").append(id);
        sb.append(", callType='").append(callType).append('\'');
        sb.append(", callerNumber='").append(callerNumber).append('\'');
        sb.append(", receiverNumber='").append(receiverNumber).append('\'');
        sb.append(", startTime=").append(startTime);
        sb.append(", endTime=").append(endTime);
        sb.append(", callerId=").append(callerId);
        sb.append(", receiverId=").append(receiverId);
        sb.append('}');
        return sb.toString();
    }
}
