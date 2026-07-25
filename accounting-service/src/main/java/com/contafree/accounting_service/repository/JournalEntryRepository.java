package com.contafree.accounting_service.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.contafree.accounting_service.entity.JournalEntry;

public interface JournalEntryRepository extends JpaRepository<JournalEntry, UUID> {

    Page<JournalEntry> findAllByJournalId(UUID journalId, Pageable pageable);

    Optional<JournalEntry> findByIdempotencyKey(String idempotencyKey);

    List<JournalEntry> findAllByJournalId(UUID journalId);
}
