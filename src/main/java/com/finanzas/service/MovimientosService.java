package com.finanzas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finanzas.repository.MovimientosRepository;

@Service
public class MovimientosService {

	@Autowired
	MovimientosRepository movimientosRepository;
	
	
}
