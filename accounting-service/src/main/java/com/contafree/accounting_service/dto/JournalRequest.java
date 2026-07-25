package com.contafree.accounting_service.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Datos para crear un período contable")
public class JournalRequest {

    @NotBlank
    @Size(max = 100)
    @Schema(description = "Nombre del período", example = "Enero 2026")
    private String name;

    @NotNull
    @Schema(description = "Fecha de inicio del período", example = "2026-01-01")
    private LocalDate startDate;

    @NotNull
    @Schema(description = "Fecha de cierre del período", example = "2026-01-31")
    private LocalDate endDate;
}
