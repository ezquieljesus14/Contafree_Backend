package com.contafree.transactions_service.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.contafree.common.dto.ApiResponse;
import com.contafree.transactions_service.dto.CategoryRequestDto;
import com.contafree.transactions_service.dto.CategoryResponseDto;
import com.contafree.transactions_service.entity.Category.TransactionType;
import com.contafree.transactions_service.service.CategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Gestión de categorías de transacciones")
@SecurityRequirement(name = "bearerAuth")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "Listar categorías", description = "Filtra por tipo si se proporciona el parámetro")
    public ResponseEntity<ApiResponse<List<CategoryResponseDto>>> getAll(
            @RequestParam(required = false) TransactionType type) {
        return ResponseEntity.ok(ApiResponse.ok(categoryService.getAll(type)));
    }

    @PostMapping
    @Operation(summary = "Crear categoría")
    public ResponseEntity<ApiResponse<CategoryResponseDto>> create(
            @Valid @RequestBody CategoryRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(categoryService.create(request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar categoría", description = "Falla si la categoría tiene transacciones asociadas")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        categoryService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
