package com.contafree.accounting_service.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.contafree.accounting_service.dto.ChartOfAccountsRequest;
import com.contafree.accounting_service.entity.ChartOfAccounts;
import com.contafree.accounting_service.entity.ChartOfAccounts.AccountType;
import com.contafree.accounting_service.mapper.ChartOfAccountsMapper;
import com.contafree.accounting_service.repository.ChartOfAccountsRepository;
import com.contafree.common.exception.DuplicateResourceException;
import com.contafree.common.exception.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class ChartOfAccountsServiceTest {

    @Mock private ChartOfAccountsRepository repository;
    @Mock private ChartOfAccountsMapper mapper;

    @InjectMocks
    private ChartOfAccountsService service;

    @Test
    void create_throwsDuplicate_whenCodeAlreadyExists() {
        ChartOfAccountsRequest req = request("1000", "Caja");
        when(repository.existsByCode("1000")).thenReturn(true);

        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(DuplicateResourceException.class);

        verify(repository, never()).save(any());
    }

    @Test
    void create_savesAccount_whenCodeIsNew() {
        ChartOfAccountsRequest req = request("9999", "Cuenta nueva");
        ChartOfAccounts saved = ChartOfAccounts.builder()
                .id(UUID.randomUUID())
                .code("9999")
                .build();

        when(repository.existsByCode("9999")).thenReturn(false);
        when(repository.save(any())).thenReturn(saved);

        service.create(req);

        verify(repository).save(any(ChartOfAccounts.class));
    }

    @Test
    void findById_throwsNotFound_whenDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    private ChartOfAccountsRequest request(String code, String name) {
        ChartOfAccountsRequest req = new ChartOfAccountsRequest();
        req.setCode(code);
        req.setName(name);
        req.setType(AccountType.ASSET);
        return req;
    }
}
