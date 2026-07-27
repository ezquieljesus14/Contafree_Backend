package com.contafree.transactions_service.mapper;

import org.mapstruct.Mapper;

import com.contafree.transactions_service.dto.CategoryRequestDto;
import com.contafree.transactions_service.dto.CategoryResponseDto;
import com.contafree.transactions_service.entity.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryResponseDto toResponseDto(Category category);
    Category toEntity(CategoryRequestDto request);
}
