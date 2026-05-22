package com.fundflow.fund;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FundRepository extends JpaRepository<Fund, UUID> {
    Optional<Fund> findByIsin(String isin);

    boolean existsByIsin(String isin);
}
