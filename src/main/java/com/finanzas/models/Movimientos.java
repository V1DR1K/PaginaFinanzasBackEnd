package com.finanzas.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
public class Movimientos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipo; // "ingreso" o "egreso"

    @Enumerated(EnumType.STRING)
    private TipoMovimiento tipoMovimiento; // GASTO, INVERSION, SALARIO

    private double cantidad;
    private LocalDate fecha;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "categoria_id")
    private Long categoriaId;
}
