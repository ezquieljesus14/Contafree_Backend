package com.contafree.accounting_service.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.contafree.accounting_service.dto.JournalRequest;
import com.contafree.accounting_service.dto.JournalResponse;
import com.contafree.accounting_service.entity.Journal;
import com.contafree.accounting_service.entity.Journal.JournalStatus;
import com.contafree.accounting_service.mapper.JournalMapper;
import com.contafree.accounting_service.repository.JournalRepository;
import com.contafree.common.exception.ResourceNotFoundException;
import com.contafree.common.exception.UnauthorizedException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JournalService {

    private final JournalRepository repository;
    private final JournalMapper mapper;

    @Transactional(readOnly = true)
    public List<JournalResponse> findAllOpen() {
        return repository.findAllByStatus(JournalStatus.OPEN).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public JournalResponse findById(UUID id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Journal not found: " + id));
    }

    @Transactional
    public JournalResponse create(JournalRequest request, UUID userId) {
        Journal journal = Journal.builder()
                .name(request.getName())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .createdBy(userId)
                .build();
        return mapper.toResponse(repository.save(journal));
    }

    @Transactional
    public JournalResponse close(UUID id) {
        Journal journal = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Journal not found: " + id));

        if (journal.getStatus() == JournalStatus.CLOSED) {
            throw new UnauthorizedException("Journal is already closed");
        }
        journal.setStatus(JournalStatus.CLOSED);
        journal.setClosedAt(LocalDateTime.now());
        return mapper.toResponse(repository.save(journal));
    }
}
