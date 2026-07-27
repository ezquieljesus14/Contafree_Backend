package com.contafree.transactions_service.mapper;

import org.mapstruct.Mapper;

import com.contafree.transactions_service.dto.TransactionRequestDto;
import com.contafree.transactions_service.dto.TransactionResponseDto;
import com.contafree.transactions_service.entity.Transaction;

@Mapper(componentModel = "spring", uses = CategoryMapper.class)
public interface TransactionMapper {
    TransactionResponseDto toResponseDto(Transaction transaction);
    Transaction toEntity(TransactionRequestDto request);
}
