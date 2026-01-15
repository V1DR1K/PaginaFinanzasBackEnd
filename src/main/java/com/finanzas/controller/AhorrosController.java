package com.finanzas.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.finanzas.models.Movimientos;
import com.finanzas.service.MovimientosService;

@RestController
@RequestMapping("/ahorros/")
public class AhorrosController {
	
	@Autowired
	MovimientosService movimientosService;
	
	
	@GetMapping("getEstadisticasEsteMes")
    public List<Movimientos> getEstadisticasEsteMes(){
		List<Movimientos> movimientosDeEsteMes = new ArrayList<Movimientos>();
		movimientosDeEsteMes = movimientosService.getMovimientosMes();
		return movimientosDeEsteMes;
    }



}
