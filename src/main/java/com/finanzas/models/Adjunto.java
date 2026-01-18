package com.finanzas.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "adjuntos",
       indexes = @Index(name = "idx_adjuntos_movimiento", columnList = "movimiento_id"))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Adjunto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "movimiento_id", nullable = false)
    private Long movimientoId;

    @Column(name = "nombre_archivo", nullable = false)
    private String nombreArchivo;

    @Column(name = "tipo_archivo", nullable = false, length = 100)
    private String tipoArchivo;

    @Column(nullable = false)
    private Long tamano;

    @Column(nullable = false, length = 500)
    private String url;

    @Column(name = "fecha_subida", updatable = false)
    private LocalDateTime fechaSubida;

    @PrePersist
    protected void onCreate() {
        fechaSubida = LocalDateTime.now();
    }
}
