package com.finanzas.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DolarTipoDTO {
    private String nombre; // ej: oficial, blue, etc.
    private Float compra;
    private Float venta;
    private String fecha;
}
