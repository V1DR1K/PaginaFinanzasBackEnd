package com.finanzas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.finanzas.models.Movimientos;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MovimientosRepository extends JpaRepository<Movimientos, Long> {

    List<Movimientos> findByUserId(Long userId);

    Optional<Movimientos> findByIdAndUserId(Long id, Long userId);

    List<Movimientos> findByUserIdAndTipo(Long userId, String tipo);

    List<Movimientos> findByUserIdAndTipoAndFechaGreaterThanEqual(Long userId, String tipo, LocalDate fecha);

    List<Movimientos> findByUserIdAndTipoAndCantidadLessThanAndFechaGreaterThanEqual(
        Long userId, String tipo, int cantidad, LocalDate fecha);

    @Query("SELECT m.tipoMovimiento, SUM(m.cantidad) FROM Movimientos m " +
           "WHERE m.userId = :userId AND m.tipo = 'egreso' " +
           "AND YEAR(m.fecha) = YEAR(:mes) AND MONTH(m.fecha) = MONTH(:mes) " +
           "GROUP BY m.tipoMovimiento")
    List<Object[]> findGastosPorCategoriaYMes(@Param("userId") Long userId, @Param("mes") LocalDate mes);
}
