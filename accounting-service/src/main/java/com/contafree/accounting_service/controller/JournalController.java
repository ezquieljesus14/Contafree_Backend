package com.contafree.accounting_service.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.contafree.accounting_service.dto.JournalRequest;
import com.contafree.accounting_service.dto.JournalResponse;
import com.contafree.accounting_service.service.JournalService;
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
@RequestMapping("/api/v1/accounting/journals")
@RequiredArgsConstructor
@Tag(name = "Journals", description = "Períodos contables")
@SecurityRequirement(name = "bearerAuth")
public class JournalController {

    private final JournalService service;

    @Operation(summary = "Listar períodos abiertos", tags = {"Journals"})
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200", description = "Lista de períodos abiertos",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = ApiResponse.class, subTypes = {JournalResponse.class}))
    )
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<JournalResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.ok(service.findAllOpen()));
    }

    @Operation(summary = "Obtener período por ID", tags = {"Journals"})
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "Período encontrado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiResponse.class, subTypes = {JournalResponse.class}))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Período no encontrado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiResponse.class)))
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<JournalResponse>> findById(
            @Parameter(description = "ID del período", required = true) @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(service.findById(id)));
    }

    @Operation(summary = "Crear período contable", tags = {"Journals"})
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "201", description = "Período creado",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = ApiResponse.class, subTypes = {JournalResponse.class}))
    )
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<JournalResponse>> create(
            @Parameter(description = "Datos del período", required = true)
            @Valid @RequestBody JournalRequest request,
            Authentication auth) {
        UUID userId = UUID.fromString((String) auth.getPrincipal());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(service.create(request, userId)));
    }

    @Operation(summary = "Cerrar período contable", description = "Solo ROLE_ADMIN. Un período cerrado no admite más asientos.", tags = {"Journals"})
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "Período cerrado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiResponse.class, subTypes = {JournalResponse.class}))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Período no encontrado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiResponse.class)))
    })
    @PutMapping(value = "/{id}/close", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<JournalResponse>> close(
            @Parameter(description = "ID del período", required = true) @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(service.close(id)));
    }
}
