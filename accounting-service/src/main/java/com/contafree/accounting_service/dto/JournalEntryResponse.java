package com.contafree.accounting_service.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.contafree.accounting_service.entity.JournalEntry.EntryStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JournalEntryResponse {
    private UUID id;
    private UUID journalId;
    private String description;
    private LocalDate entryDate;
    private EntryStatus status;
    private String idempotencyKey;
    private UUID createdBy;
    private LocalDateTime createdAt;
    private List<JournalLineResponse> lines;
}
