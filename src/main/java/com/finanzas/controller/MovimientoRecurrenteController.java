package com.finanzas.controller;

import com.finanzas.config.CustomUserDetails;
import com.finanzas.models.MovimientoRecurrente;
import com.finanzas.models.dto.MovimientoRecurrenteRequest;
import com.finanzas.service.MovimientoRecurrenteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/movimientos/recurrentes")
public class MovimientoRecurrenteController {

    @Autowired
    private MovimientoRecurrenteService movimientoRecurrenteService;

    @GetMapping
    public ResponseEntity<List<MovimientoRecurrente>> getMovimientosRecurrentes(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        List<MovimientoRecurrente> movimientos = movimientoRecurrenteService.findAllByUserId(userId);
        return ResponseEntity.ok(movimientos);
    }

    @GetMapping("/activos")
    public ResponseEntity<List<MovimientoRecurrente>> getMovimientosRecurrentesActivos(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        List<MovimientoRecurrente> movimientos = movimientoRecurrenteService.findActivosByUserId(userId);
        return ResponseEntity.ok(movimientos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovimientoRecurrente> getMovimientoRecurrenteById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        return movimientoRecurrenteService.findByIdAndUserId(id, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createMovimientoRecurrente(
            @Valid @RequestBody MovimientoRecurrenteRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            Long userId = userDetails.getUserId();
            MovimientoRecurrente movimiento = movimientoRecurrenteService.create(request, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(movimiento);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> updateMovimientoRecurrente(
            @PathVariable Long id,
            @Valid @RequestBody MovimientoRecurrenteRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            Long userId = userDetails.getUserId();
            MovimientoRecurrente movimiento = movimientoRecurrenteService.update(id, request, userId);
            return ResponseEntity.ok(movimiento);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<?> toggleActivo(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            Long userId = userDetails.getUserId();
            MovimientoRecurrente movimiento = movimientoRecurrenteService.toggleActivo(id, userId);
            return ResponseEntity.ok(movimiento);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/{id}/ejecutar")
    public ResponseEntity<?> ejecutarManualmente(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            Long userId = userDetails.getUserId();
            Map<String, Object> response = movimientoRecurrenteService.ejecutarManualmente(id, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMovimientoRecurrente(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            Long userId = userDetails.getUserId();
            movimientoRecurrenteService.delete(id, userId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
