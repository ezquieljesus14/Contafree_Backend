package com.contafree.transactions_service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.contafree.transactions_service.entity.Category.TransactionType;
import com.contafree.transactions_service.entity.Transaction.TransactionStatus;
import lombok.Data;

@Data
public class TransactionResponseDto {
    private UUID id;
    private TransactionType type;
    private BigDecimal amount;
    private String description;
    private LocalDate date;
    private CategoryResponseDto category;
    private UUID contactId;
    private TransactionStatus status;
    private UUID journalEntryId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
