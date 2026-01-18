package com.finanzas.models.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaRequest {

    @NotBlank(message = "El nombre es requerido")
    private String nombre;

    @NotBlank(message = "El tipo es requerido")
    @Pattern(regexp = "ingreso|egreso", message = "El tipo debe ser 'ingreso' o 'egreso'")
    private String tipo;

    private String icono;
    private String color;
    private Long categoriaPadreId;
}
