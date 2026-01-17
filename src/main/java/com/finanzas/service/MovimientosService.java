package com.finanzas.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finanzas.models.Movimientos;
import com.finanzas.repository.MovimientosRepository;

@Service
public class MovimientosService {

	@Autowired
	MovimientosRepository movimientosRepository;

	public List<Movimientos> findAllMovimientos() {
		return movimientosRepository.findAll().reversed();
	}

	public Optional<Movimientos> findMovimientoById(Integer id) {
		return movimientosRepository.findById(id);
	}

	public Movimientos newMovimiento(Movimientos movimiento) {
		movimiento.setId(null); // asegurar creación de nuevo registro
		return movimientosRepository.save(movimiento);
	}

	public Optional<Movimientos> editMovimiento(Integer id, Movimientos movimiento) {
		return movimientosRepository.findById(id)
				.map(existing -> {
					movimiento.setId(id);
					return movimientosRepository.save(movimiento);
				});
	}

	public boolean deleteMovimiento(Integer id) {
		if (movimientosRepository.existsById(id)) {
			movimientosRepository.deleteById(id);
			return true;
		}
		return false;
	}
}
