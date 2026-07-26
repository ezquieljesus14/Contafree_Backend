package com.contafree.accounting_service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.contafree.accounting_service.dto.JournalEntryRequest;
import com.contafree.accounting_service.dto.JournalEntryResponse;
import com.contafree.accounting_service.dto.JournalLineRequest;
import com.contafree.accounting_service.entity.ChartOfAccounts;
import com.contafree.accounting_service.entity.Journal;
import com.contafree.accounting_service.entity.Journal.JournalStatus;
import com.contafree.accounting_service.entity.JournalEntry;
import com.contafree.accounting_service.entity.JournalEntry.EntryStatus;
import com.contafree.accounting_service.entity.JournalLine.LineType;
import com.contafree.accounting_service.mapper.JournalMapper;
import com.contafree.accounting_service.repository.ChartOfAccountsRepository;
import com.contafree.accounting_service.repository.JournalEntryRepository;
import com.contafree.accounting_service.repository.JournalRepository;
import com.contafree.common.exception.ResourceNotFoundException;
import com.contafree.common.exception.UnauthorizedException;

@ExtendWith(MockitoExtension.class)
class JournalEntryServiceTest {

    @Mock private JournalEntryRepository entryRepository;
    @Mock private JournalRepository journalRepository;
    @Mock private ChartOfAccountsRepository accountRepository;
    @Mock private JournalMapper mapper;

    @InjectMocks
    private JournalEntryService service;

    private UUID userId;
    private UUID journalId;
    private Journal openJournal;
    private Journal closedJournal;
    private ChartOfAccounts account1;
    private ChartOfAccounts account2;

    @BeforeEach
    void setUp() {
        userId    = UUID.randomUUID();
        journalId = UUID.randomUUID();

        openJournal = Journal.builder()
                .id(journalId)
                .name("Ejercicio 2026")
                .status(JournalStatus.OPEN)
                .build();

        closedJournal = Journal.builder()
                .id(journalId)
                .name("Ejercicio 2025")
                .status(JournalStatus.CLOSED)
                .build();

        account1 = ChartOfAccounts.builder().code("1000").name("Caja").build();
        account2 = ChartOfAccounts.builder().code("4000").name("Proveedores").build();
    }

    // ── Idempotencia ──────────────────────────────────────────────────────────

    @Test
    void create_returnsExisting_whenIdempotencyKeyAlreadyUsed() {
        String key = "key-123";
        JournalEntry existing = JournalEntry.builder().id(UUID.randomUUID()).build();
        JournalEntryResponse expectedResponse = JournalEntryResponse.builder().id(UUID.randomUUID()).build();

        when(entryRepository.findByIdempotencyKey(key)).thenReturn(Optional.of(existing));
        when(mapper.toEntryResponse(existing)).thenReturn(expectedResponse);

        JournalEntryResponse result = service.create(validRequest(), key, userId);

        assertThat(result).isSameAs(expectedResponse);
        verify(journalRepository, never()).findById(any());
        verify(entryRepository, never()).save(any());
    }

    // ── Validación de journal ─────────────────────────────────────────────────

    @Test
    void create_throwsUnauthorized_whenJournalIsClosed() {
        String key = "key-456";
        when(entryRepository.findByIdempotencyKey(key)).thenReturn(Optional.empty());
        when(journalRepository.findById(journalId)).thenReturn(Optional.of(closedJournal));

        assertThatThrownBy(() -> service.create(validRequest(), key, userId))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("closed");
    }

    @Test
    void create_throwsNotFound_whenJournalDoesNotExist() {
        String key = "key-789";
        when(entryRepository.findByIdempotencyKey(key)).thenReturn(Optional.empty());
        when(journalRepository.findById(journalId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(validRequest(), key, userId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── Validación de balance ─────────────────────────────────────────────────

    @Test
    void create_throwsIllegalArgument_whenLessThanTwoLines() {
        JournalEntryRequest req = validRequest();
        req.setLines(List.of(line("1000", LineType.DEBIT, "100.00")));

        String key = "key-001";
        when(entryRepository.findByIdempotencyKey(key)).thenReturn(Optional.empty());
        when(journalRepository.findById(journalId)).thenReturn(Optional.of(openJournal));

        assertThatThrownBy(() -> service.create(req, key, userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("at least 2 lines");
    }

    @Test
    void create_throwsIllegalArgument_whenDebitsDoNotEqualCredits() {
        JournalEntryRequest req = validRequest();
        req.setLines(List.of(
                line("1000", LineType.DEBIT,  "100.00"),
                line("4000", LineType.CREDIT, "200.00")
        ));

        String key = "key-002";
        when(entryRepository.findByIdempotencyKey(key)).thenReturn(Optional.empty());
        when(journalRepository.findById(journalId)).thenReturn(Optional.of(openJournal));

        assertThatThrownBy(() -> service.create(req, key, userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must equal credits");
    }

    @Test
    void create_succeeds_whenBalancedAndJournalOpen() {
        String key = "key-ok";
        JournalEntry saved = JournalEntry.builder().id(UUID.randomUUID()).build();
        JournalEntryResponse expectedResponse = JournalEntryResponse.builder().id(UUID.randomUUID()).build();

        when(entryRepository.findByIdempotencyKey(key)).thenReturn(Optional.empty());
        when(journalRepository.findById(journalId)).thenReturn(Optional.of(openJournal));
        when(accountRepository.findByCode("1000")).thenReturn(Optional.of(account1));
        when(accountRepository.findByCode("4000")).thenReturn(Optional.of(account2));
        when(entryRepository.save(any())).thenReturn(saved);
        when(mapper.toEntryResponse(saved)).thenReturn(expectedResponse);

        JournalEntryResponse result = service.create(validRequest(), key, userId);

        assertThat(result).isSameAs(expectedResponse);
        verify(entryRepository).save(any());
    }

    // ── Update ────────────────────────────────────────────────────────────────

    @Test
    void update_throwsUnauthorized_whenEntryIsPosted() {
        UUID entryId = UUID.randomUUID();
        JournalEntry posted = JournalEntry.builder()
                .id(entryId)
                .status(EntryStatus.POSTED)
                .build();

        when(entryRepository.findById(entryId)).thenReturn(Optional.of(posted));

        assertThatThrownBy(() -> service.update(entryId, validRequest()))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("DRAFT");
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    @Test
    void delete_throwsUnauthorized_whenEntryIsPosted() {
        UUID entryId = UUID.randomUUID();
        JournalEntry posted = JournalEntry.builder()
                .id(entryId)
                .status(EntryStatus.POSTED)
                .build();

        when(entryRepository.findById(entryId)).thenReturn(Optional.of(posted));

        assertThatThrownBy(() -> service.delete(entryId))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("DRAFT");
    }

    @Test
    void delete_succeeds_whenEntryIsDraft() {
        UUID entryId = UUID.randomUUID();
        JournalEntry draft = JournalEntry.builder()
                .id(entryId)
                .status(EntryStatus.DRAFT)
                .build();

        when(entryRepository.findById(entryId)).thenReturn(Optional.of(draft));

        service.delete(entryId);

        verify(entryRepository).delete(draft);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private JournalEntryRequest validRequest() {
        JournalEntryRequest req = new JournalEntryRequest();
        req.setJournalId(journalId);
        req.setDescription("Test asiento");
        req.setEntryDate(LocalDate.now());
        req.setLines(List.of(
                line("1000", LineType.DEBIT,  "500.00"),
                line("4000", LineType.CREDIT, "500.00")
        ));
        return req;
    }

    private JournalLineRequest line(String code, LineType type, String amount) {
        JournalLineRequest l = new JournalLineRequest();
        l.setAccountCode(code);
        l.setType(type);
        l.setAmount(new BigDecimal(amount));
        return l;
    }
}
