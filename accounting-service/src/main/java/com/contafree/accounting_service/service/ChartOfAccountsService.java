package com.contafree.accounting_service.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.contafree.accounting_service.dto.ChartOfAccountsRequest;
import com.contafree.accounting_service.dto.ChartOfAccountsResponse;
import com.contafree.accounting_service.entity.ChartOfAccounts;
import com.contafree.accounting_service.mapper.ChartOfAccountsMapper;
import com.contafree.accounting_service.repository.ChartOfAccountsRepository;
import com.contafree.common.exception.DuplicateResourceException;
import com.contafree.common.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChartOfAccountsService {

    private final ChartOfAccountsRepository repository;
    private final ChartOfAccountsMapper mapper;

    @Transactional(readOnly = true)
    public List<ChartOfAccountsResponse> findAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ChartOfAccountsResponse findById(UUID id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + id));
    }

    @Transactional
    public ChartOfAccountsResponse create(ChartOfAccountsRequest request) {
        if (repository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Account code already exists: " + request.getCode());
        }
        ChartOfAccounts entity = ChartOfAccounts.builder()
                .code(request.getCode())
                .name(request.getName())
                .type(request.getType())
                .parentCode(request.getParentCode())
                .build();
        return mapper.toResponse(repository.save(entity));
    }
}
