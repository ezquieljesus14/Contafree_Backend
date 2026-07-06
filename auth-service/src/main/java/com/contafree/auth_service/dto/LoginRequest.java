package com.contafree.auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Credenciales de acceso")
public class LoginRequest {

    @NotBlank
    @Email
    @Schema(description = "Email del usuario", example = "test@contafree.com")
    private String email;

    @NotBlank
    @Schema(description = "Contraseña del usuario", example = "Test1234!")
    private String password;
}
