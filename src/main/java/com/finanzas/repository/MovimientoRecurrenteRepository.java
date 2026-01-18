package com.finanzas.repository;

import com.finanzas.models.MovimientoRecurrente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MovimientoRecurrenteRepository extends JpaRepository<MovimientoRecurrente, Long> {

    List<MovimientoRecurrente> findByUserId(Long userId);

    List<MovimientoRecurrente> findByUserIdAndActivoTrue(Long userId);

    Optional<MovimientoRecurrente> findByIdAndUserId(Long id, Long userId);

    List<MovimientoRecurrente> findByActivoTrueAndProximaEjecucionLessThanEqual(LocalDate fecha);
}
