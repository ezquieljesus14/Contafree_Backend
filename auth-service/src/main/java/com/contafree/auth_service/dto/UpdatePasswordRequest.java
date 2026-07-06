package com.contafree.auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para cambiar la contraseña")
public class UpdatePasswordRequest {

    @NotBlank
    @Schema(description = "Contraseña actual del usuario", example = "Test1234!")
    private String currentPassword;

    @NotBlank
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z\\d]).{10,}$",
        message = "must be at least 10 characters and contain one uppercase letter, one lowercase letter, one digit, and one special character"
    )
    @Schema(description = "Nueva contraseña (mín. 10 caracteres, 1 mayúscula, 1 minúscula, 1 número, 1 especial)", example = "NewPass1234!")
    private String newPassword;
}
