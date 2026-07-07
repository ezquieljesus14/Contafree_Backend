package com.contafree.accounting_service.mapper;

import org.mapstruct.Mapper;

import com.contafree.accounting_service.dto.ChartOfAccountsResponse;
import com.contafree.accounting_service.entity.ChartOfAccounts;

@Mapper(componentModel = "spring")
public interface ChartOfAccountsMapper {

    ChartOfAccountsResponse toResponse(ChartOfAccounts entity);
}
