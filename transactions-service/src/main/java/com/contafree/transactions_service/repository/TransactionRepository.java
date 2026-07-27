package com.contafree.transactions_service.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contafree.transactions_service.entity.Transaction;
import com.contafree.transactions_service.entity.Transaction.TransactionStatus;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findByUserIdAndDeletedFalse(UUID userId);
    List<Transaction> findByUserIdAndDateBetweenAndDeletedFalse(UUID userId, LocalDate from, LocalDate to);
    List<Transaction> findByStatusAndDeletedFalse(TransactionStatus status);
    Optional<Transaction> findByIdAndDeletedFalse(UUID id);
}
