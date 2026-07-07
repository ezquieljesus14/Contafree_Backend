package com.contafree.auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Token de refresco")
public class RefreshRequest {

    @NotBlank
    @Schema(description = "Refresh token obtenido en el login", example = "550e8400-e29b-41d4-a716-446655440000")
    private String refreshToken;
}