package com.fundflow.transaction;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TxRepository extends JpaRepository<Transaction, UUID> {
}
