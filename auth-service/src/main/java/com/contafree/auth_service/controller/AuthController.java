package com.contafree.auth_service.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.contafree.auth_service.dto.LoginRequest;
import com.contafree.auth_service.dto.LoginResponse;
import com.contafree.auth_service.dto.RefreshRequest;
import com.contafree.auth_service.dto.RegisterRequest;
import com.contafree.auth_service.dto.UpdatePasswordRequest;
import com.contafree.auth_service.dto.UserResponse;
import com.contafree.auth_service.service.AuthService;
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
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Autenticación y gestión de usuarios")
public class AuthController {

    private final AuthService authService;

    // ─── register ────────────────────────────────────────────────────────────

    @Operation(
        summary = "Registrar nuevo usuario",
        description = "Solo accesible con ROLE_ADMIN. Contraseña: mín. 10 caracteres, 1 mayúscula, 1 minúscula, 1 número y 1 especial.",
        tags = {"Auth"}
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Usuario creado correctamente",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiResponse.class, subTypes = {UserResponse.class})
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Datos inválidos o contraseña no cumple los requisitos",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "409",
            description = "Email ya registrado",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiResponse.class)
            )
        )
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<UserResponse>> register(
            @Parameter(description = "Datos del nuevo usuario", required = true)
            @Valid @RequestBody RegisterRequest request) {
        UserResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    // ─── login ────────────────────────────────────────────────────────────────

    @Operation(
        summary = "Login",
        description = "Devuelve un access token (15 min) y un refresh token (7 días).",
        tags = {"Auth"}
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Login correcto",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiResponse.class, subTypes = {LoginResponse.class})
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Email o contraseña incorrectos",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiResponse.class)
            )
        )
    })
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Parameter(description = "Credenciales de acceso", required = true)
            @Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    // ─── refresh ──────────────────────────────────────────────────────────────

    @Operation(
        summary = "Renovar access token",
        description = "Usa el refresh token para obtener un nuevo access token sin volver a loguearse.",
        tags = {"Auth"}
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Access token renovado",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiResponse.class, subTypes = {LoginResponse.class})
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Refresh token inválido o expirado",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiResponse.class)
            )
        )
    })
    @PostMapping(value = "/refresh", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(
            @Parameter(description = "Refresh token activo", required = true)
            @Valid @RequestBody RefreshRequest request) {
        LoginResponse response = authService.refresh(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    // ─── logout ───────────────────────────────────────────────────────────────

    @Operation(
        summary = "Logout",
        description = "Invalida el refresh token. El access token expirará por sí solo (15 min).",
        tags = {"Auth"}
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Sesión cerrada correctamente"
    )
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping(value = "/logout", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Void>> logout(
            @Parameter(description = "Refresh token a invalidar", required = true)
            @Valid @RequestBody RefreshRequest request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    // ─── update password ──────────────────────────────────────────────────────

    @Operation(
        summary = "Cambiar contraseña",
        description = "Requiere la contraseña actual para verificación. La nueva debe cumplir los mismos requisitos que en el registro.",
        tags = {"Auth"}
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Contraseña actualizada"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Nueva contraseña no cumple los requisitos",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Contraseña actual incorrecta",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiResponse.class)
            )
        )
    })
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping(value = "/me", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Void>> updatePassword(
            @Parameter(description = "Contraseña actual y nueva contraseña", required = true)
            @Valid @RequestBody UpdatePasswordRequest request,
            Authentication auth) {
        UUID userId = UUID.fromString((String) auth.getPrincipal());
        authService.updatePassword(userId, request);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    // ─── get current user ─────────────────────────────────────────────────────

    @Operation(
        summary = "Datos del usuario autenticado",
        tags = {"Auth"}
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Datos del usuario",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiResponse.class, subTypes = {UserResponse.class})
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Usuario no encontrado",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ApiResponse.class)
            )
        )
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(Authentication auth) {
        UUID userId = UUID.fromString((String) auth.getPrincipal());
        return authService.getCurrentUser(userId)
                .map(dto -> ResponseEntity.ok(ApiResponse.ok(dto)))
                .orElse(ResponseEntity.status(404).body(
                        ApiResponse.error("User not found", "NOT_FOUND")));
    }
}
