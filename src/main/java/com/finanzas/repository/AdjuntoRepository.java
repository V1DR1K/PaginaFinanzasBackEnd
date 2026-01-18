package com.finanzas.repository;

import com.finanzas.models.Adjunto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdjuntoRepository extends JpaRepository<Adjunto, Long> {

    @Query("SELECT a FROM Adjunto a JOIN Movimientos m ON a.movimientoId = m.id WHERE a.movimientoId = :movimientoId AND m.userId = :userId")
    List<Adjunto> findByMovimientoIdAndUserId(@Param("movimientoId") Long movimientoId, @Param("userId") Long userId);

    @Query("SELECT a FROM Adjunto a JOIN Movimientos m ON a.movimientoId = m.id WHERE a.id = :adjuntoId AND m.userId = :userId")
    Optional<Adjunto> findByIdAndUserId(@Param("adjuntoId") Long adjuntoId, @Param("userId") Long userId);
}
