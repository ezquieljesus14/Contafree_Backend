package com.contafree.accounting_service.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.contafree.accounting_service.entity.JournalLine.LineType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JournalLineResponse {
    private UUID id;
    private String accountCode;
    private String accountName;
    private LineType type;
    private BigDecimal amount;
}
