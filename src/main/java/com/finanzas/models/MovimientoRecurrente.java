package com.finanzas.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimientos_recurrentes",
       indexes = {
           @Index(name = "idx_movimientos_recurrentes_user", columnList = "user_id"),
           @Index(name = "idx_movimientos_recurrentes_proxima", columnList = "proxima_ejecucion")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoRecurrente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String tipo; // "ingreso" o "egreso"

    @Column(name = "tipo_movimiento", nullable = false, length = 50)
    private String tipoMovimiento;

    @Column(name = "categoria_id")
    private Long categoriaId;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal cantidad;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Frecuencia frecuencia;

    @Column(name = "dia_ejecucion", nullable = false)
    private Integer diaEjecucion;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Column(name = "proxima_ejecucion")
    private LocalDate proximaEjecucion;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "ultima_modificacion")
    private LocalDateTime ultimaModificacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        ultimaModificacion = LocalDateTime.now();
        if (proximaEjecucion == null) {
            proximaEjecucion = calcularProximaEjecucion(fechaInicio);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        ultimaModificacion = LocalDateTime.now();
    }

    public LocalDate calcularProximaEjecucion(LocalDate desde) {
        LocalDate base = desde;

        switch (frecuencia) {
            case DIARIA:
                return base.plusDays(1);
            case SEMANAL:
                return base.plusWeeks(1);
            case QUINCENAL:
                return base.plusDays(15);
            case MENSUAL:
                LocalDate proximoMes = base.plusMonths(1);
                int diaMax = proximoMes.lengthOfMonth();
                int diaFinal = Math.min(diaEjecucion, diaMax);
                return proximoMes.withDayOfMonth(diaFinal);
            case ANUAL:
                return base.plusYears(1);
            default:
                return base;
        }
    }

    public enum Frecuencia {
        DIARIA, SEMANAL, QUINCENAL, MENSUAL, ANUAL
    }
}
