package com.finanzas.models.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CryptoRequest {

    @NotBlank(message = "El instId es requerido")
    private String instId;

    @NotBlank(message = "El símbolo es requerido")
    private String symbol;

    @NotNull(message = "La cantidad es requerida")
    @DecimalMin(value = "0.00000001", message = "La cantidad debe ser mayor a 0")
    private BigDecimal amount;

    @NotNull(message = "El porcentaje es requerido")
    @DecimalMin(value = "0.0", message = "El porcentaje debe ser mayor o igual a 0")
    @DecimalMax(value = "100.0", message = "El porcentaje no puede ser mayor a 100")
    private BigDecimal percentage;

    private String source;  // Opcional: 'bitget' o 'binance'
    private String display;
    private Boolean activo = true;
}
