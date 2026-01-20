package com.finanzas.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "gastos_inusuales",
       indexes = @Index(name = "idx_gastos_inusuales_user", columnList = "user_id"))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GastoInusual {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "movimiento_id", nullable = false)
    private Long movimientoId;

    @Column(nullable = false, length = 50)
    private String tipo;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal cantidad;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "promedio_historico", nullable = false, precision = 15, scale = 2)
    private BigDecimal promedioHistorico;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal desviacion;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String razon;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "fecha_deteccion", updatable = false)
    private LocalDateTime fechaDeteccion;

    @PrePersist
    protected void onCreate() {
        fechaDeteccion = LocalDateTime.now();
    }
}
