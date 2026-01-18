package com.finanzas.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categorias",
       uniqueConstraints = @UniqueConstraint(columnNames = {"nombre", "tipo", "user_id", "categoria_padre_id"}),
       indexes = {
           @Index(name = "idx_categorias_user", columnList = "user_id"),
           @Index(name = "idx_categorias_padre", columnList = "categoria_padre_id")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 10)
    private String tipo; // "ingreso" o "egreso"

    @Column(length = 50)
    private String icono;

    @Column(length = 20)
    private String color;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "categoria_padre_id")
    private Long categoriaPadreId;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @OneToMany(mappedBy = "categoriaPadreId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("nombre ASC")
    private List<Categoria> subcategorias = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }
}
