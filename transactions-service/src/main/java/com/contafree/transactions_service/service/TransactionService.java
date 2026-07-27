package com.contafree.transactions_service.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.contafree.common.exception.ResourceNotFoundException;
import com.contafree.common.exception.UnauthorizedException;
import com.contafree.transactions_service.client.AccountingServiceClient;
import com.contafree.transactions_service.client.JournalLookupResult;
import com.contafree.transactions_service.dto.TransactionRequestDto;
import com.contafree.transactions_service.dto.TransactionResponseDto;
import com.contafree.transactions_service.entity.Category;
import com.contafree.transactions_service.entity.Transaction;
import com.contafree.transactions_service.entity.Transaction.TransactionStatus;
import com.contafree.transactions_service.mapper.TransactionMapper;
import com.contafree.transactions_service.repository.CategoryRepository;
import com.contafree.transactions_service.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionMapper transactionMapper;
    private final AccountingServiceClient accountingClient;

    @Transactional
    public TransactionResponseDto create(TransactionRequestDto request,
                                         String idempotencyKey,
                                         UUID userId,
                                         String authToken) {
        Optional<Transaction> existing = transactionRepository.findByIdempotencyKey(idempotencyKey);
        if (existing.isPresent()) {
            Transaction txn = existing.get();
            if (txn.getStatus() == TransactionStatus.COMPLETED) {
                return transactionMapper.toResponseDto(txn);
            }
            return processAccounting(txn, authToken);
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Categoría no encontrada: " + request.getCategoryId()));

        Transaction txn = Transaction.builder()
                .userId(userId)
                .type(request.getType())
                .amount(request.getAmount())
                .description(request.getDescription())
                .date(request.getDate())
                .category(category)
                .contactId(request.getContactId())
                .idempotencyKey(idempotencyKey)
                .build();
        txn = transactionRepository.save(txn);

        return processAccounting(txn, authToken);
    }

    @Transactional(readOnly = true)
    public List<TransactionResponseDto> getByUser(UUID userId) {
        return transactionRepository.findByUserIdAndDeletedFalse(userId)
                .stream().map(transactionMapper::toResponseDto).toList();
    }

    @Transactional(readOnly = true)
    public List<TransactionResponseDto> getByUserAndDateRange(UUID userId, LocalDate from, LocalDate to) {
        return transactionRepository.findByUserIdAndDateBetweenAndDeletedFalse(userId, from, to)
                .stream().map(transactionMapper::toResponseDto).toList();
    }

    @Transactional(readOnly = true)
    public TransactionResponseDto getById(UUID id, UUID userId) {
        Transaction txn = transactionRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transacción no encontrada: " + id));
        if (!txn.getUserId().equals(userId)) {
            throw new UnauthorizedException("Acceso denegado");
        }
        return transactionMapper.toResponseDto(txn);
    }

    @Transactional
    public void delete(UUID id, UUID userId) {
        Transaction txn = transactionRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transacción no encontrada: " + id));
        if (!txn.getUserId().equals(userId)) {
            throw new UnauthorizedException("Acceso denegado");
        }
        txn.setDeleted(true);
        transactionRepository.save(txn);
    }

    private TransactionResponseDto processAccounting(Transaction txn, String authToken) {
        JournalLookupResult lookup = accountingClient.findJournalForDate(authToken, txn.getDate());

        if (lookup instanceof JournalLookupResult.Found found) {
            Optional<UUID> entryId = accountingClient.createJournalEntry(
                    authToken, found.journalId(), txn.getDate(),
                    txn.getDescription(),
                    txn.getCategory().getDebitAccountCode(),
                    txn.getCategory().getCreditAccountCode(),
                    txn.getAmount(), txn.getIdempotencyKey());
            if (entryId.isPresent()) {
                txn.setStatus(TransactionStatus.COMPLETED);
                txn.setJournalEntryId(entryId.get());
                txn.setStatusReason(null);
            } else {
                txn.setStatusReason("ACCOUNTING_UNAVAILABLE");
            }
        } else if (lookup instanceof JournalLookupResult.NoOpenPeriod) {
            txn.setStatusReason("NO_OPEN_PERIOD");
        } else if (lookup instanceof JournalLookupResult.NoMatchingPeriod) {
            txn.setStatusReason("NO_MATCHING_PERIOD");
        } else {
            txn.setStatusReason("ACCOUNTING_UNAVAILABLE");
        }

        return transactionMapper.toResponseDto(transactionRepository.save(txn));
    }
}
