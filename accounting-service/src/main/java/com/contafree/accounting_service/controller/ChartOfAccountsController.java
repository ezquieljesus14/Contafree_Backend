package com.contafree.accounting_service.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.contafree.accounting_service.dto.ChartOfAccountsRequest;
import com.contafree.accounting_service.dto.ChartOfAccountsResponse;
import com.contafree.accounting_service.service.ChartOfAccountsService;
import com.contafree.common.dto.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/accounting/chart-of-accounts")
@RequiredArgsConstructor
@Tag(name = "Chart of Accounts", description = "Plan de cuentas contable")
@SecurityRequirement(name = "bearerAuth")
public class ChartOfAccountsController {

    private final ChartOfAccountsService service;

    @Operation(summary = "Listar todas las cuentas contables", tags = {"Chart of Accounts"})
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200", description = "Lista de cuentas",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = ApiResponse.class, subTypes = {ChartOfAccountsResponse.class}))
    )
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<ChartOfAccountsResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.ok(service.findAll()));
    }

    @Operation(summary = "Obtener cuenta por ID", tags = {"Chart of Accounts"})
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "Cuenta encontrada",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiResponse.class, subTypes = {ChartOfAccountsResponse.class}))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cuenta no encontrada",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiResponse.class)))
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ChartOfAccountsResponse>> findById(
            @Parameter(description = "ID de la cuenta", required = true) @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(service.findById(id)));
    }

    @Operation(summary = "Crear cuenta contable", description = "Solo accesible con ROLE_ADMIN", tags = {"Chart of Accounts"})
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201", description = "Cuenta creada",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiResponse.class, subTypes = {ChartOfAccountsResponse.class}))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Código ya existe",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiResponse.class)))
    })
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ChartOfAccountsResponse>> create(
            @Parameter(description = "Datos de la cuenta", required = true)
            @Valid @RequestBody ChartOfAccountsRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(service.create(request)));
    }
}
