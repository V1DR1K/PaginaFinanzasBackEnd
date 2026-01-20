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

	public List<Movimientos> findAllMovimientosByUserId(Long userId) {
		return movimientosRepository.findByUserId(userId).reversed();
	}

	public Optional<Movimientos> findMovimientoByIdAndUserId(Long id, Long userId) {
		return movimientosRepository.findByIdAndUserId(id, userId);
	}

	public Movimientos newMovimiento(Movimientos movimiento) {
		movimiento.setId(null); // asegurar creación de nuevo registro
		return movimientosRepository.save(movimiento);
	}

	public Optional<Movimientos> editMovimiento(Long id, Movimientos movimiento, Long userId) {
		return movimientosRepository.findByIdAndUserId(id, userId)
				.map(existing -> {
					movimiento.setId(id);
					movimiento.setUserId(userId); // Asegurar que no cambie el userId
					return movimientosRepository.save(movimiento);
				});
	}

	public boolean deleteMovimiento(Long id, Long userId) {
		Optional<Movimientos> movimiento = movimientosRepository.findByIdAndUserId(id, userId);
		if (movimiento.isPresent()) {
			movimientosRepository.deleteById(id);
			return true;
		}
		return false;
	}
}
