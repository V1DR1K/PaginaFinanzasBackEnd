package com.finanzas.repository;

import com.finanzas.models.GastoInusual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface GastoInusualRepository extends JpaRepository<GastoInusual, Long> {

    List<GastoInusual> findByUserIdOrderByFechaDeteccionDesc(Long userId);

    List<GastoInusual> findByUserIdAndFechaGreaterThanEqualOrderByFechaDesc(Long userId, LocalDate fecha);
}
