package com.example.cdrservice.repository;

import com.example.cdrservice.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий для работы с сущностью {@link Customer}.
 * Предоставляет методы для поиска клиентов по номеру телефона (MSISDN).
 */
@Repository
public interface MsisdnRepository extends JpaRepository<Customer, Long> {

    /**
     * Находит клиента по номеру телефона (MSISDN).
     *
     * @param msisdn номер телефона
     * @return {@link Optional}, содержащий клиента, если он найден, или пустой {@link Optional}, если клиент не найден
     */
    Optional<Customer> findByMsisdn(String msisdn);
}