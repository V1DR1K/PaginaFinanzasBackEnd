package com.finanzas.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.finanzas.models.Movimientos;
import com.finanzas.service.MovimientosService;

@RestController
@RequestMapping("/movimiento")
public class MovimientosController {

	@Autowired
	private MovimientosService movimientosService;

	@GetMapping("/findAllMovimientos")
	public ResponseEntity<List<Movimientos>> findAllMovimientos() {
		List<Movimientos> lista = movimientosService.findAllMovimientos();
		return ResponseEntity.ok(lista);
	}

	@GetMapping("/findMovimientoById/{id}")
	public ResponseEntity<Movimientos> findMovimientoById(@PathVariable int id) {
		Optional<Movimientos> movimiento = movimientosService.findMovimientoById(id);
		return movimiento.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PostMapping("/newMovimiento")
	public ResponseEntity<Movimientos> newMovimiento(@RequestBody Movimientos movimiento) {
		Movimientos creado = movimientosService.newMovimiento(movimiento);
		return ResponseEntity.status(201).body(creado);
	}

	@PostMapping("/editMovimiento/{id}")
	public ResponseEntity<Movimientos> editMovimiento(@PathVariable int id, @RequestBody Movimientos movimiento) {
		Optional<Movimientos> actualizado = movimientosService.editMovimiento(id, movimiento);
		return actualizado.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@DeleteMapping("/deleteMovimiento/{id}")
	public ResponseEntity<Void> deleteMovimiento(@PathVariable int id) {
		boolean eliminado = movimientosService.deleteMovimiento(id);
		if (eliminado) {
			return ResponseEntity.noContent().build();
		} else {
			return ResponseEntity.notFound().build();
		}
	}
}