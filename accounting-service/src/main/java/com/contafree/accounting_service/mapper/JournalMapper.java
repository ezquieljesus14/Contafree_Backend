package com.contafree.accounting_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.contafree.accounting_service.dto.JournalEntryResponse;
import com.contafree.accounting_service.dto.JournalLineResponse;
import com.contafree.accounting_service.dto.JournalResponse;
import com.contafree.accounting_service.entity.Journal;
import com.contafree.accounting_service.entity.JournalEntry;
import com.contafree.accounting_service.entity.JournalLine;

@Mapper(componentModel = "spring")
public interface JournalMapper {

    JournalResponse toResponse(Journal entity);

    @Mapping(target = "journalId", source = "journal.id")
    JournalEntryResponse toEntryResponse(JournalEntry entity);

    @Mapping(target = "accountCode", source = "account.code")
    @Mapping(target = "accountName", source = "account.name")
    JournalLineResponse toLineResponse(JournalLine entity);
}
