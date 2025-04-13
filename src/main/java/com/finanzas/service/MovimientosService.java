package com.finanzas.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finanzas.models.Movimientos;
import com.finanzas.repository.MovimientosRepository;

@Service
public class MovimientosService {

	@Autowired
	MovimientosRepository movimientosRepository;
	
	public void save(Movimientos movimiento) {
		movimientosRepository.save(movimiento);
	}
	
	public List<Movimientos> getMovimientosMes(){
		String mesActual = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")); 
		return movimientosRepository.findAllByMes(mesActual);
	}
	
}
