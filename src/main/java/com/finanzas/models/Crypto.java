package com.finanzas.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cryptos",
       indexes = {
           @Index(name = "idx_cryptos_user", columnList = "user_id"),
           @Index(name = "idx_cryptos_user_instid", columnList = "user_id, inst_id")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Crypto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "inst_id", nullable = false, length = 50)
    private String instId;  // 'BTCUSDT', 'ETHUSDT'

    @Column(nullable = false, length = 20)
    private String symbol;  // 'BTC', 'ETH'

    @Column(nullable = false, precision = 20, scale = 8)
    private BigDecimal amount;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal percentage;

    @Column(length = 20)
    private String source;  // 'bitget' o 'binance'

    @Column(length = 50)
    private String display;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
        if (activo == null) {
            activo = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}
