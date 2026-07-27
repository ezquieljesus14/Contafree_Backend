package com.contafree.transactions_service.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.contafree.common.dto.ApiResponse;
import com.contafree.transactions_service.dto.TransactionRequestDto;
import com.contafree.transactions_service.dto.TransactionResponseDto;
import com.contafree.transactions_service.service.TransactionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Gestión de transacciones financieras")
@SecurityRequirement(name = "bearerAuth")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    @Operation(summary = "Crear transacción", description = "Requiere header Idempotency-Key (UUID)")
    public ResponseEntity<ApiResponse<TransactionResponseDto>> create(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody TransactionRequestDto request,
            Authentication auth,
            HttpServletRequest httpRequest) {
        UUID userId = UUID.fromString((String) auth.getPrincipal());
        String authToken = httpRequest.getHeader("Authorization");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(transactionService.create(request, idempotencyKey, userId, authToken)));
    }

    @GetMapping
    @Operation(summary = "Listar transacciones del usuario autenticado",
               description = "Filtra por rango de fechas si se proporcionan ?from y ?to")
    public ResponseEntity<ApiResponse<List<TransactionResponseDto>>> getAll(
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to,
            Authentication auth) {
        UUID userId = UUID.fromString((String) auth.getPrincipal());
        List<TransactionResponseDto> result = (from != null && to != null)
                ? transactionService.getByUserAndDateRange(userId, from, to)
                : transactionService.getByUser(userId);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener transacción por ID")
    public ResponseEntity<ApiResponse<TransactionResponseDto>> getById(
            @PathVariable UUID id,
            Authentication auth) {
        UUID userId = UUID.fromString((String) auth.getPrincipal());
        return ResponseEntity.ok(ApiResponse.ok(transactionService.getById(id, userId)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar transacción (soft delete)")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable UUID id,
            Authentication auth) {
        UUID userId = UUID.fromString((String) auth.getPrincipal());
        transactionService.delete(id, userId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
