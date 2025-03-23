package com.example.cdrservice.repository;

import com.example.cdrservice.entity.CDRRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для работы с сущностью {@link CDRRecord}.
 */
@Repository
public interface CDRRepository extends JpaRepository<CDRRecord, Long> {
}