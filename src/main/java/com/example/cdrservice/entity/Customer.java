package com.example.cdrservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Сущность, представляющая клиента.
 * Содержит информацию о номере телефона (MSISDN) и списках входящих и исходящих звонков.
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "customer")
public class Customer {

    /**
     * Уникальный идентификатор клиента.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    /**
     * Номер телефона клиента (MSISDN).
     */
    @Column(name = "msisdn", unique = true, nullable = false)
    private String msisdn;

    /**
     * Список входящих звонков для клиента.
     */
    @OneToMany(mappedBy = "receiverCustomer", fetch = FetchType.EAGER)
    private List<CDRRecord> incomingCalls;

    /**
     * Список исходящих звонков для клиента.
     */
    @OneToMany(mappedBy = "callerCustomer", fetch = FetchType.EAGER)
    private List<CDRRecord> receivedCalls;

    /**
     * Конструктор для создания клиента с указанным номером телефона.
     *
     * @param msisdn номер телефона клиента
     */
    public Customer(String msisdn) {
        this.msisdn = msisdn;
    }
}