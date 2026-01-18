package com.finanzas.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CambioContrasenaRequest {
    private String contrasenaActual;
    private String contrasenaNueva;
}
