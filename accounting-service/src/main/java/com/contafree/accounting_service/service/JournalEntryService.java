package com.contafree.accounting_service.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.contafree.accounting_service.dto.JournalEntryRequest;
import com.contafree.accounting_service.dto.JournalEntryResponse;
import com.contafree.accounting_service.dto.JournalLineResponse;
import com.contafree.accounting_service.entity.Journal;
import com.contafree.accounting_service.entity.Journal.JournalStatus;
import com.contafree.accounting_service.entity.JournalEntry;
import com.contafree.accounting_service.entity.JournalEntry.EntryStatus;
import com.contafree.accounting_service.entity.JournalLine;
import com.contafree.accounting_service.entity.JournalLine.LineType;
import com.contafree.accounting_service.mapper.JournalMapper;
import com.contafree.accounting_service.repository.ChartOfAccountsRepository;
import com.contafree.accounting_service.repository.JournalEntryRepository;
import com.contafree.accounting_service.repository.JournalRepository;
import com.contafree.common.exception.ResourceNotFoundException;
import com.contafree.common.exception.UnauthorizedException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JournalEntryService {

    private final JournalEntryRepository entryRepository;
    private final JournalRepository journalRepository;
    private final ChartOfAccountsRepository accountRepository;
    private final JournalMapper mapper;

    @Transactional(readOnly = true)
    public Page<JournalEntryResponse> findAll(UUID journalId, Pageable pageable) {
        return entryRepository.findAllByJournalId(journalId, pageable)
                .map(mapper::toEntryResponse);
    }

    @Transactional(readOnly = true)
    public JournalEntryResponse findById(UUID id) {
        return entryRepository.findById(id)
                .map(mapper::toEntryResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Entry not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<JournalLineResponse> findLines(UUID entryId) {
        JournalEntry entry = entryRepository.findById(entryId)
                .orElseThrow(() -> new ResourceNotFoundException("Entry not found: " + entryId));
        return entry.getLines().stream()
                .map(mapper::toLineResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public JournalEntryResponse create(JournalEntryRequest request, String idempotencyKey, UUID userId) {
        // Idempotencia: si ya existe con esa key, devuelve el existente
        return entryRepository.findByIdempotencyKey(idempotencyKey)
                .map(mapper::toEntryResponse)
                .orElseGet(() -> doCreate(request, idempotencyKey, userId));
    }

    private JournalEntryResponse doCreate(JournalEntryRequest request, String idempotencyKey, UUID userId) {
        Journal journal = journalRepository.findById(request.getJournalId())
                .orElseThrow(() -> new ResourceNotFoundException("Journal not found: " + request.getJournalId()));

        if (journal.getStatus() == JournalStatus.CLOSED) {
            throw new UnauthorizedException("Cannot add entries to a closed journal");
        }

        validateBalance(request);

        JournalEntry entry = JournalEntry.builder()
                .journal(journal)
                .description(request.getDescription())
                .entryDate(request.getEntryDate())
                .idempotencyKey(idempotencyKey)
                .createdBy(userId)
                .build();

        List<JournalLine> lines = request.getLines().stream().map(lineReq -> {
            var account = accountRepository.findByCode(lineReq.getAccountCode())
                    .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + lineReq.getAccountCode()));
            return JournalLine.builder()
                    .journalEntry(entry)
                    .account(account)
                    .type(lineReq.getType())
                    .amount(lineReq.getAmount())
                    .build();
        }).collect(Collectors.toList());

        entry.setLines(lines);
        return mapper.toEntryResponse(entryRepository.save(entry));
    }

    @Transactional
    public JournalEntryResponse update(UUID id, JournalEntryRequest request) {
        JournalEntry entry = entryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entry not found: " + id));

        if (entry.getStatus() != EntryStatus.DRAFT) {
            throw new UnauthorizedException("Only DRAFT entries can be updated");
        }

        validateBalance(request);

        entry.setDescription(request.getDescription());
        entry.setEntryDate(request.getEntryDate());
        entry.getLines().clear();

        request.getLines().forEach(lineReq -> {
            var account = accountRepository.findByCode(lineReq.getAccountCode())
                    .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + lineReq.getAccountCode()));
            entry.getLines().add(JournalLine.builder()
                    .journalEntry(entry)
                    .account(account)
                    .type(lineReq.getType())
                    .amount(lineReq.getAmount())
                    .build());
        });

        return mapper.toEntryResponse(entryRepository.save(entry));
    }

    @Transactional
    public void delete(UUID id) {
        JournalEntry entry = entryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entry not found: " + id));

        if (entry.getStatus() != EntryStatus.DRAFT) {
            throw new UnauthorizedException("Only DRAFT entries can be deleted");
        }
        entryRepository.delete(entry);
    }

    // ── Regla de negocio: total débito == total crédito, mín 2 líneas ──────────

    private void validateBalance(JournalEntryRequest request) {
        if (request.getLines().size() < 2) {
            throw new IllegalArgumentException("A journal entry must have at least 2 lines");
        }

        BigDecimal totalDebit = request.getLines().stream()
                .filter(l -> l.getType() == LineType.DEBIT)
                .map(l -> l.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCredit = request.getLines().stream()
                .filter(l -> l.getType() == LineType.CREDIT)
                .map(l -> l.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalDebit.compareTo(totalCredit) != 0) {
            throw new IllegalArgumentException(
                    "Debits (" + totalDebit + ") must equal credits (" + totalCredit + ")");
        }
    }
}
