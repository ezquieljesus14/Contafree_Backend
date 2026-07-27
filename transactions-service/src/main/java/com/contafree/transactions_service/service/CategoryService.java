package com.contafree.transactions_service.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.contafree.common.exception.BusinessException;
import com.contafree.common.exception.DuplicateResourceException;
import com.contafree.common.exception.ResourceNotFoundException;
import com.contafree.transactions_service.dto.CategoryRequestDto;
import com.contafree.transactions_service.dto.CategoryResponseDto;
import com.contafree.transactions_service.entity.Category;
import com.contafree.transactions_service.entity.Category.TransactionType;
import com.contafree.transactions_service.mapper.CategoryMapper;
import com.contafree.transactions_service.repository.CategoryRepository;
import com.contafree.transactions_service.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final CategoryMapper categoryMapper;

    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getAll(TransactionType type) {
        List<Category> categories = type != null
                ? categoryRepository.findByType(type)
                : categoryRepository.findAll();
        return categories.stream().map(categoryMapper::toResponseDto).toList();
    }

    @Transactional
    public CategoryResponseDto create(CategoryRequestDto request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("name",
                    "Ya existe una categoría con el nombre: " + request.getName());
        }
        return categoryMapper.toResponseDto(
                categoryRepository.save(categoryMapper.toEntity(request)));
    }

    @Transactional
    public void delete(UUID id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Categoría no encontrada: " + id);
        }
        if (transactionRepository.existsByCategoryId(id)) {
            throw new BusinessException(
                    "No se puede eliminar una categoría con transacciones asociadas",
                    "CATEGORY_IN_USE");
        }
        categoryRepository.deleteById(id);
    }
}
