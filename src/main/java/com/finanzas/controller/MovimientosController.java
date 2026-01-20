package com.finanzas.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.finanzas.config.CustomUserDetails;
import com.finanzas.models.Movimientos;
import com.finanzas.service.MovimientosService;

@RestController
@RequestMapping("/movimiento")
public class MovimientosController {

	@Autowired
	private MovimientosService movimientosService;

	@GetMapping("/findAllMovimientos")
	public ResponseEntity<List<Movimientos>> findAllMovimientos(
			@AuthenticationPrincipal CustomUserDetails userDetails) {
		Long userId = userDetails.getUserId();
		List<Movimientos> lista = movimientosService.findAllMovimientosByUserId(userId);
		return ResponseEntity.ok(lista);
	}

	@GetMapping("/findMovimientoById/{id}")
	public ResponseEntity<Movimientos> findMovimientoById(@PathVariable Long id,
			@AuthenticationPrincipal CustomUserDetails userDetails) {
		Long userId = userDetails.getUserId();
		Optional<Movimientos> movimiento = movimientosService.findMovimientoByIdAndUserId(id, userId);
		return movimiento.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PostMapping("/newMovimiento")
	public ResponseEntity<Movimientos> newMovimiento(@RequestBody Movimientos movimiento,
			@AuthenticationPrincipal CustomUserDetails userDetails) {
		Long userId = userDetails.getUserId();
		movimiento.setUserId(userId);
		Movimientos creado = movimientosService.newMovimiento(movimiento);
		return ResponseEntity.status(201).body(creado);
	}

	@PostMapping("/editMovimiento/{id}")
	public ResponseEntity<Movimientos> editMovimiento(@PathVariable Long id, @RequestBody Movimientos movimiento,
			@AuthenticationPrincipal CustomUserDetails userDetails) {
		Long userId = userDetails.getUserId();
		Optional<Movimientos> actualizado = movimientosService.editMovimiento(id, movimiento, userId);
		return actualizado.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@DeleteMapping("/deleteMovimiento/{id}")
	public ResponseEntity<Void> deleteMovimiento(@PathVariable Long id,
			@AuthenticationPrincipal CustomUserDetails userDetails) {
		Long userId = userDetails.getUserId();
		boolean eliminado = movimientosService.deleteMovimiento(id, userId);
		return eliminado ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
	}
}
