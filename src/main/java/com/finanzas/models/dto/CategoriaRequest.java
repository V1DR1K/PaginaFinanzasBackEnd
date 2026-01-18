package com.finanzas.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaRequest {
    private String nombre;
    private String tipo;
    private String icono;
    private String color;
    private Long categoriaPadreId;
}
