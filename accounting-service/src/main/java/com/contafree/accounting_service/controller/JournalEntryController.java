package com.contafree.accounting_service.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.contafree.accounting_service.dto.JournalEntryRequest;
import com.contafree.accounting_service.dto.JournalEntryResponse;
import com.contafree.accounting_service.dto.JournalLineResponse;
import com.contafree.accounting_service.service.JournalEntryService;
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
@RequestMapping("/api/v1/accounting/entries")
@RequiredArgsConstructor
@Tag(name = "Journal Entries", description = "Asientos contables")
@SecurityRequirement(name = "bearerAuth")
public class JournalEntryController {

    private final JournalEntryService service;

    @Operation(summary = "Listar asientos de un período (paginado)", tags = {"Journal Entries"})
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200", description = "Página de asientos",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = ApiResponse.class, subTypes = {JournalEntryResponse.class}))
    )
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Page<JournalEntryResponse>>> findAll(
            @Parameter(description = "ID del período contable", required = true)
            @RequestParam UUID journalId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(service.findAll(journalId, pageable)));
    }

    @Operation(summary = "Obtener asiento por ID", tags = {"Journal Entries"})
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "Asiento encontrado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiResponse.class, subTypes = {JournalEntryResponse.class}))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Asiento no encontrado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiResponse.class)))
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<JournalEntryResponse>> findById(
            @Parameter(description = "ID del asiento", required = true) @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(service.findById(id)));
    }

    @Operation(summary = "Obtener líneas de un asiento", tags = {"Journal Entries"})
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200", description = "Líneas del asiento",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = ApiResponse.class, subTypes = {JournalLineResponse.class}))
    )
    @GetMapping(value = "/{id}/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<JournalLineResponse>>> findLines(
            @Parameter(description = "ID del asiento", required = true) @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(service.findLines(id)));
    }

    @Operation(
        summary = "Crear asiento contable",
        description = "Requiere header `Idempotency-Key`. Reglas: mín. 2 líneas, total débito == total crédito, todas las cuentas deben existir.",
        tags = {"Journal Entries"}
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201", description = "Asiento creado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiResponse.class, subTypes = {JournalEntryResponse.class}))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Balance incorrecto o datos inválidos",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Período o cuenta no encontrada",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiResponse.class)))
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<JournalEntryResponse>> create(
            @Parameter(description = "Clave de idempotencia (UUID)", required = true)
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Parameter(description = "Datos del asiento", required = true)
            @Valid @RequestBody JournalEntryRequest request,
            Authentication auth) {
        UUID userId = UUID.fromString((String) auth.getPrincipal());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(service.create(request, idempotencyKey, userId)));
    }

    @Operation(
        summary = "Actualizar asiento (solo DRAFT)",
        tags = {"Journal Entries"}
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "Asiento actualizado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiResponse.class, subTypes = {JournalEntryResponse.class}))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Balance incorrecto o asiento no es DRAFT",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Asiento no encontrado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiResponse.class)))
    })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<JournalEntryResponse>> update(
            @Parameter(description = "ID del asiento", required = true) @PathVariable UUID id,
            @Parameter(description = "Datos actualizados", required = true)
            @Valid @RequestBody JournalEntryRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.update(id, request)));
    }

    @Operation(summary = "Eliminar asiento (solo DRAFT)", tags = {"Journal Entries"})
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Asiento eliminado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Asiento no es DRAFT",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Asiento no encontrado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiResponse.class)))
    })
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID del asiento", required = true) @PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
