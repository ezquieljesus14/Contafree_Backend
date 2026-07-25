package com.contafree.accounting_service.dto;

import java.util.UUID;

import com.contafree.accounting_service.entity.ChartOfAccounts.AccountType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChartOfAccountsResponse {
    private UUID id;
    private String code;
    private String name;
    private AccountType type;
    private String parentCode;
    private boolean active;
}
