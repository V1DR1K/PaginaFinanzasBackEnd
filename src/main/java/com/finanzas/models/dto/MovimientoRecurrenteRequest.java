package com.finanzas.models.dto;

import com.finanzas.models.MovimientoRecurrente;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoRecurrenteRequest {

    @NotBlank(message = "El tipo es requerido")
    private String tipo;

    @NotBlank(message = "El tipo de movimiento es requerido")
    private String tipoMovimiento;

    private Long categoriaId;

    @NotNull(message = "La cantidad es requerida")
    @Positive(message = "La cantidad debe ser mayor a 0")
    private BigDecimal cantidad;

    private String descripcion;

    @NotNull(message = "La frecuencia es requerida")
    private MovimientoRecurrente.Frecuencia frecuencia;

    @NotNull(message = "El día de ejecución es requerido")
    private Integer diaEjecucion;

    @NotNull(message = "La fecha de inicio es requerida")
    private LocalDate fechaInicio;

    private LocalDate fechaFin;
}
