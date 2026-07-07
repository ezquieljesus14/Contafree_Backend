package com.contafree.auth_service.dto;

import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para registrar un nuevo usuario")
public class RegisterRequest {

    @NotBlank
    @Email
    @Schema(description = "Email del nuevo usuario", example = "nuevo@contafree.com")
    private String email;

    @NotBlank
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z\\d]).{10,}$",
        message = "must be at least 10 characters and contain one uppercase letter, one lowercase letter, one digit, and one special character"
    )
    @Schema(description = "Mín. 10 caracteres, 1 mayúscula, 1 minúscula, 1 número, 1 especial", example = "Admin1234!Cf")
    private String password;

    @NotEmpty
    @Schema(description = "Roles del usuario", example = "[\"ROLE_USER\"]")
    private Set<String> roles;
}
