package com.finanzas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.finanzas.models.Movimientos;
import com.finanzas.service.MovimientosService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/transacciones/")
public class TransaccionesController {

	@Autowired
	MovimientosService movimientosService;
	
	@PostMapping("/ingreso")
	public ResponseEntity<String> ingresarMovimiento(@RequestBody Movimientos movimiento) {
		movimientosService.save(movimiento);
		
	    return ResponseEntity.ok("Movimiento ingresado correctamente");
	}
}
