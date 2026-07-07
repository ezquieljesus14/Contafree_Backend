package com.contafree.accounting_service.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Datos para crear un asiento contable")
public class JournalEntryRequest {

    @NotNull
    @Schema(description = "ID del período contable al que pertenece el asiento", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID journalId;

    @NotBlank
    @Size(max = 255)
    @Schema(description = "Descripción del asiento", example = "Pago proveedor factura #123")
    private String description;

    @NotNull
    @Schema(description = "Fecha del asiento", example = "2026-01-15")
    private LocalDate entryDate;

    @NotEmpty
    @Valid
    @Schema(description = "Líneas del asiento (mín. 2, débitos == créditos)")
    private List<JournalLineRequest> lines;
}
