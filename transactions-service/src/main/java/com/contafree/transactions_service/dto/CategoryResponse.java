package com.contafree.transactions_service.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.contafree.transactions_service.entity.Category.TransactionType;
import lombok.Data;

@Data
public class CategoryResponse {
    private UUID id;
    private String name;
    private TransactionType type;
    private String debitAccountCode;
    private String creditAccountCode;
    private LocalDateTime createdAt;
}
