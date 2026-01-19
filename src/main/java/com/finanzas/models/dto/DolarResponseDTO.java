package com.finanzas.models.dto;

import lombok.Data;

@Data
public class DolarResponseDTO {
    private float compra;
    private float venta;
    private String fecha; // opcional
}
