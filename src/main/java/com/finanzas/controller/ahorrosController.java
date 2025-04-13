package com.finanzas.controller;

import org.springframework.web.bind.annotation.*;

import com.finanzas.models.Movimientos;

@RestController
@RequestMapping("/api/ahorros/")
@CrossOrigin(origins = "http://localhost:4200")

public class ahorrosController {

	@GetMapping("recuperarAhorros")
    public void recuperarAhorrosMes(){
		Movimientos m = new Movimientos();
		
    }



}
