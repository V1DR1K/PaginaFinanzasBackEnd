package com.finanzas.repository;

import com.finanzas.models.EventoRecordatorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventoRecordatorioRepository extends JpaRepository<EventoRecordatorio, Long> {
    List<EventoRecordatorio> findByUsuarioId(Long usuarioId);
    List<EventoRecordatorio> findByFecha(LocalDate fecha);
    List<EventoRecordatorio> findByFechaAndUsuarioEmail(LocalDate fecha, String email);
}
