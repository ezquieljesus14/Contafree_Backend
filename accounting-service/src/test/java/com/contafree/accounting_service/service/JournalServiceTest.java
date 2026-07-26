package com.contafree.accounting_service.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.contafree.accounting_service.dto.JournalRequest;
import com.contafree.accounting_service.entity.Journal;
import com.contafree.accounting_service.entity.Journal.JournalStatus;
import com.contafree.accounting_service.mapper.JournalMapper;
import com.contafree.accounting_service.repository.JournalRepository;
import com.contafree.common.exception.ResourceNotFoundException;
import com.contafree.common.exception.UnauthorizedException;

@ExtendWith(MockitoExtension.class)
class JournalServiceTest {

    @Mock private JournalRepository repository;
    @Mock private JournalMapper mapper;

    @InjectMocks
    private JournalService service;

    @Test
    void create_savesJournal() {
        UUID userId = UUID.randomUUID();
        JournalRequest req = new JournalRequest();
        req.setName("Ejercicio 2026");
        req.setStartDate(LocalDate.of(2026, 1, 1));
        req.setEndDate(LocalDate.of(2026, 12, 31));

        Journal saved = Journal.builder().id(UUID.randomUUID()).build();
        when(repository.save(any())).thenReturn(saved);

        service.create(req, userId);

        verify(repository).save(any(Journal.class));
    }

    @Test
    void close_throwsUnauthorized_whenAlreadyClosed() {
        UUID id = UUID.randomUUID();
        Journal closed = Journal.builder()
                .id(id)
                .status(JournalStatus.CLOSED)
                .build();

        when(repository.findById(id)).thenReturn(Optional.of(closed));

        assertThatThrownBy(() -> service.close(id))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("already closed");
    }

    @Test
    void close_closesJournal_whenOpen() {
        UUID id = UUID.randomUUID();
        Journal open = Journal.builder()
                .id(id)
                .status(JournalStatus.OPEN)
                .build();

        when(repository.findById(id)).thenReturn(Optional.of(open));
        when(repository.save(any())).thenReturn(open);

        service.close(id);

        verify(repository).save(open);
    }

    @Test
    void findById_throwsNotFound_whenDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
