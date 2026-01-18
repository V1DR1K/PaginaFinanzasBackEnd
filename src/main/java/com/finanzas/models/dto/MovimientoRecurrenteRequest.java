package com.finanzas.models.dto;

import com.finanzas.models.MovimientoRecurrente;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoRecurrenteRequest {
    private String tipo;
    private String tipoMovimiento;
    private Long categoriaId;
    private BigDecimal cantidad;
    private String descripcion;
    private MovimientoRecurrente.Frecuencia frecuencia;
    private Integer diaEjecucion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
}
