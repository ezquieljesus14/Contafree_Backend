package com.contafree.accounting_service.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.contafree.accounting_service.entity.Journal.JournalStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JournalResponse {
    private UUID id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private JournalStatus status;
    private UUID createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime closedAt;
}
