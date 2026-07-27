package com.contafree.transactions_service.dto;

import com.contafree.transactions_service.entity.Category.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryRequestDto {

    @NotBlank
    @Size(max = 100)
    @Schema(description = "Nombre de la categoría", example = "Servicios prestados")
    private String name;

    @NotNull
    @Schema(description = "Tipo de transacción", example = "INCOME")
    private TransactionType type;

    @NotBlank
    @Size(max = 10)
    @Schema(description = "Código PGC de la cuenta a debitar", example = "572")
    private String debitAccountCode;

    @NotBlank
    @Size(max = 10)
    @Schema(description = "Código PGC de la cuenta a acreditar", example = "705")
    private String creditAccountCode;
}
