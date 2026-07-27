package com.contafree.transactions_service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.contafree.transactions_service.entity.Category.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TransactionRequestDto {

    @NotNull
    @Schema(description = "Tipo de transacción", example = "INCOME")
    private TransactionType type;

    @NotNull
    @Positive
    @Digits(integer = 10, fraction = 2)
    @Schema(description = "Importe de la transacción", example = "1500.00")
    private BigDecimal amount;

    @NotBlank
    @Size(max = 255)
    @Schema(description = "Descripción de la transacción", example = "Factura cliente ABC")
    private String description;

    @NotNull
    @PastOrPresent
    @Schema(description = "Fecha de la transacción", example = "2026-07-26")
    private LocalDate date;

    @NotNull
    @Schema(description = "ID de la categoría")
    private UUID categoryId;

    @Schema(description = "ID del contacto asociado (opcional)")
    private UUID contactId;
}
