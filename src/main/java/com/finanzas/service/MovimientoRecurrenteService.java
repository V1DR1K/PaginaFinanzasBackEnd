package com.finanzas.service;

import com.finanzas.models.Movimientos;
import com.finanzas.models.MovimientoRecurrente;
import com.finanzas.models.dto.MovimientoRecurrenteRequest;
import com.finanzas.repository.MovimientoRecurrenteRepository;
import com.finanzas.repository.MovimientosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MovimientoRecurrenteService {

    @Autowired
    private MovimientoRecurrenteRepository movimientoRecurrenteRepository;

    @Autowired
    private MovimientosRepository movimientosRepository;

    public List<MovimientoRecurrente> findAllByUserId(Long userId) {
        return movimientoRecurrenteRepository.findByUserId(userId);
    }

    public List<MovimientoRecurrente> findActivosByUserId(Long userId) {
        return movimientoRecurrenteRepository.findByUserIdAndActivoTrue(userId);
    }

    public Optional<MovimientoRecurrente> findByIdAndUserId(Long id, Long userId) {
        return movimientoRecurrenteRepository.findByIdAndUserId(id, userId);
    }

    @Transactional
    public MovimientoRecurrente create(MovimientoRecurrenteRequest request, Long userId) {
        // Validaciones de campos requeridos
        if (request.getTipo() == null || request.getTipo().trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo es requerido");
        }

        if (request.getTipoMovimiento() == null || request.getTipoMovimiento().trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de movimiento es requerido");
        }

        if (request.getCantidad() == null) {
            throw new IllegalArgumentException("La cantidad es requerida");
        }

        if (request.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }

        if (request.getFrecuencia() == null) {
            throw new IllegalArgumentException("La frecuencia es requerida");
        }

        if (request.getDiaEjecucion() == null) {
            throw new IllegalArgumentException("El día de ejecución es requerido");
        }

        if (request.getFechaInicio() == null) {
            throw new IllegalArgumentException("La fecha de inicio es requerida");
        }

        if (request.getFechaInicio().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de inicio debe ser hoy o posterior");
        }

        if (request.getFechaFin() != null && request.getFechaFin().isBefore(request.getFechaInicio())) {
            throw new IllegalArgumentException("La fecha fin debe ser posterior a la fecha de inicio");
        }

        MovimientoRecurrente movimiento = new MovimientoRecurrente();
        movimiento.setTipo(request.getTipo());
        movimiento.setTipoMovimiento(request.getTipoMovimiento());
        movimiento.setCategoriaId(request.getCategoriaId());
        movimiento.setCantidad(request.getCantidad());
        movimiento.setDescripcion(request.getDescripcion());
        movimiento.setFrecuencia(request.getFrecuencia());
        movimiento.setDiaEjecucion(request.getDiaEjecucion());
        movimiento.setFechaInicio(request.getFechaInicio());
        movimiento.setFechaFin(request.getFechaFin());
        movimiento.setUserId(userId);
        movimiento.setActivo(true);

        return movimientoRecurrenteRepository.save(movimiento);
    }

    @Transactional
    public MovimientoRecurrente update(Long id, MovimientoRecurrenteRequest request, Long userId) {
        // Buscar el movimiento recurrente
        MovimientoRecurrente movimiento = movimientoRecurrenteRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new IllegalArgumentException("Movimiento recurrente no encontrado"));

        // Validaciones (igual que en create)
        if (request.getTipo() == null || request.getTipo().trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo es requerido");
        }

        if (request.getTipoMovimiento() == null || request.getTipoMovimiento().trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de movimiento es requerido");
        }

        if (request.getCantidad() == null) {
            throw new IllegalArgumentException("La cantidad es requerida");
        }

        if (request.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }

        if (request.getFrecuencia() == null) {
            throw new IllegalArgumentException("La frecuencia es requerida");
        }

        if (request.getDiaEjecucion() == null) {
            throw new IllegalArgumentException("El día de ejecución es requerido");
        }

        if (request.getFechaInicio() == null) {
            throw new IllegalArgumentException("La fecha de inicio es requerida");
        }

        if (request.getFechaFin() != null && request.getFechaFin().isBefore(request.getFechaInicio())) {
            throw new IllegalArgumentException("La fecha fin debe ser posterior a la fecha de inicio");
        }

        // Actualizar campos
        movimiento.setTipo(request.getTipo());
        movimiento.setTipoMovimiento(request.getTipoMovimiento());
        movimiento.setCategoriaId(request.getCategoriaId());
        movimiento.setCantidad(request.getCantidad());
        movimiento.setDescripcion(request.getDescripcion());
        movimiento.setFrecuencia(request.getFrecuencia());
        movimiento.setDiaEjecucion(request.getDiaEjecucion());
        movimiento.setFechaInicio(request.getFechaInicio());
        movimiento.setFechaFin(request.getFechaFin());

        // Recalcular próxima ejecución si cambió la frecuencia o el día
        movimiento.setProximaEjecucion(movimiento.calcularProximaEjecucion(LocalDate.now()));

        return movimientoRecurrenteRepository.save(movimiento);
    }

    @Transactional
    public MovimientoRecurrente toggleActivo(Long id, Long userId) {
        MovimientoRecurrente movimiento = movimientoRecurrenteRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new IllegalArgumentException("Movimiento recurrente no encontrado"));

        movimiento.setActivo(!movimiento.getActivo());

        if (movimiento.getActivo()) {
            // Recalcular próxima ejecución
            movimiento.setProximaEjecucion(movimiento.calcularProximaEjecucion(LocalDate.now()));
        }

        return movimientoRecurrenteRepository.save(movimiento);
    }

    @Transactional
    public Map<String, Object> ejecutarManualmente(Long id, Long userId) {
        MovimientoRecurrente recurrente = movimientoRecurrenteRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new IllegalArgumentException("Movimiento recurrente no encontrado"));

        // Crear movimiento normal
        Movimientos movimiento = new Movimientos();
        movimiento.setTipo(recurrente.getTipo());
        movimiento.setTipoMovimiento(com.finanzas.models.TipoMovimiento.valueOf(recurrente.getTipoMovimiento().toUpperCase()));
        movimiento.setCategoriaId(recurrente.getCategoriaId());
        movimiento.setCantidad(recurrente.getCantidad().intValue());
        movimiento.setDescripcion(recurrente.getDescripcion() + " (Ejecutado manualmente)");
        movimiento.setFecha(LocalDate.now());
        movimiento.setUserId(userId);

        Movimientos movimientoCreado = movimientosRepository.save(movimiento);

        // Actualizar próxima ejecución
        recurrente.setProximaEjecucion(recurrente.calcularProximaEjecucion(recurrente.getProximaEjecucion()));
        movimientoRecurrenteRepository.save(recurrente);

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Movimiento ejecutado exitosamente");
        response.put("movimientoCreado", movimientoCreado);

        return response;
    }

    @Scheduled(cron = "0 0 1 * * *") // Todos los días a la 1 AM
    @Transactional
    public void ejecutarMovimientosRecurrentesAutomatico() {
        LocalDate hoy = LocalDate.now();
        List<MovimientoRecurrente> pendientes = movimientoRecurrenteRepository
                .findByActivoTrueAndProximaEjecucionLessThanEqual(hoy);

        for (MovimientoRecurrente recurrente : pendientes) {
            try {
                // Crear movimiento normal
                Movimientos movimiento = new Movimientos();
                movimiento.setTipo(recurrente.getTipo());
                movimiento.setTipoMovimiento(com.finanzas.models.TipoMovimiento.valueOf(recurrente.getTipoMovimiento().toUpperCase()));
                movimiento.setCategoriaId(recurrente.getCategoriaId());
                movimiento.setCantidad(recurrente.getCantidad().intValue());
                movimiento.setDescripcion(recurrente.getDescripcion() + " (Automático)");
                movimiento.setFecha(hoy);
                movimiento.setUserId(recurrente.getUserId());
                movimientosRepository.save(movimiento);

                // Actualizar próxima ejecución
                LocalDate proximaFecha = recurrente.calcularProximaEjecucion(recurrente.getProximaEjecucion());
                recurrente.setProximaEjecucion(proximaFecha);

                // Si llegó a fechaFin, desactivar
                if (recurrente.getFechaFin() != null && proximaFecha.isAfter(recurrente.getFechaFin())) {
                    recurrente.setActivo(false);
                }

                movimientoRecurrenteRepository.save(recurrente);
            } catch (Exception e) {
                // Log error pero continuar con los demás
                System.err.println("Error ejecutando movimiento recurrente ID " + recurrente.getId() + ": " + e.getMessage());
            }
        }
    }

    @Transactional
    public void delete(Long id, Long userId) {
        MovimientoRecurrente movimiento = movimientoRecurrenteRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new IllegalArgumentException("Movimiento recurrente no encontrado o no pertenece al usuario"));
        movimientoRecurrenteRepository.delete(movimiento);
    }
}
