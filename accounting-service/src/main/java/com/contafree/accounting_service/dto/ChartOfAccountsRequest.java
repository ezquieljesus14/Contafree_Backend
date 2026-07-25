package com.contafree.accounting_service.dto;

import com.contafree.accounting_service.entity.ChartOfAccounts.AccountType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Datos para crear una cuenta contable")
public class ChartOfAccountsRequest {

    @NotBlank
    @Size(max = 20)
    @Schema(description = "Código único de la cuenta", example = "1000")
    private String code;

    @NotBlank
    @Size(max = 150)
    @Schema(description = "Nombre descriptivo de la cuenta", example = "Caja")
    private String name;

    @NotNull
    @Schema(description = "Tipo de cuenta", example = "ASSET")
    private AccountType type;

    @Size(max = 20)
    @Schema(description = "Código de la cuenta padre (null si es raíz)", example = "1000")
    private String parentCode;
}
