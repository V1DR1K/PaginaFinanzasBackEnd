package com.finanzas.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "insights",
       indexes = @Index(name = "idx_insights_user_leido", columnList = "user_id, leido"))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Insight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TipoInsight tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SeveridadInsight severidad;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Column(precision = 15, scale = 2)
    private BigDecimal monto;

    @Column(length = 100)
    private String categoria;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(nullable = false)
    private Boolean leido = false;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @PrePersist
    protected void onCreate() {
        fecha = LocalDateTime.now();
        if (leido == null) {
            leido = false;
        }
    }

    public enum TipoInsight {
        GASTO_AUMENTADO,
        GASTO_DISMINUIDO,
        GASTO_INUSUAL,
        AHORRO_POTENCIAL,
        PATRON_DETECTADO,
        ALERTA_PRESUPUESTO
    }

    public enum SeveridadInsight {
        INFO,
        WARNING,
        ALERT
    }
}
