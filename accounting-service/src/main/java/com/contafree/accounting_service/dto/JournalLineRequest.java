package com.contafree.accounting_service.dto;

import java.math.BigDecimal;

import com.contafree.accounting_service.entity.JournalLine.LineType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Línea de un asiento contable (debe o haber)")
public class JournalLineRequest {

    @NotBlank
    @Schema(description = "Código de la cuenta contable", example = "1000")
    private String accountCode;

    @NotNull
    @Schema(description = "Tipo de movimiento", example = "DEBIT")
    private LineType type;

    @NotNull
    @DecimalMin(value = "0.01", message = "El importe debe ser mayor que 0")
    @Schema(description = "Importe de la línea", example = "1500.00")
    private BigDecimal amount;
}
