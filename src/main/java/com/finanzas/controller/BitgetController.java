package com.finanzas.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.finanzas.service.BitgetService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/bitget")
@RequiredArgsConstructor
public class BitgetController {

	private final BitgetService bitgetService;

	/**
	 * Obtiene el balance de la cuenta en Bitget
	 * GET /api/bitget/balance
	 */
	@GetMapping("/balance")
	public ResponseEntity<String> getBalance() {
		try {
			String balance = bitgetService.getAccountBalance();
			return ResponseEntity.ok(balance);
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body("{\"error\": \"" + e.getMessage() + "\"}");
		}
	}

	/**
	 * Obtiene información de la cuenta
	 * GET /api/bitget/account
	 */
	@GetMapping("/account")
	public ResponseEntity<String> getAccountInfo() {
		try {
			String accountInfo = bitgetService.getAccountInfo();
			return ResponseEntity.ok(accountInfo);
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body("{\"error\": \"" + e.getMessage() + "\"}");
		}
	}

	/**
	 * Obtiene el historial de órdenes
	 * GET /api/bitget/orders?symbol=BTCUSDT&startTime=1234567890&endTime=1234567890&limit=100
	 */
	@GetMapping("/orders")
	public ResponseEntity<String> getOrderHistory(@RequestParam(required = false, defaultValue = "BTCUSDT") String symbol,
			@RequestParam(required = false, defaultValue = "0") String startTime,
			@RequestParam(required = false, defaultValue = "9999999999999") String endTime,
			@RequestParam(required = false, defaultValue = "100") int limit) {
		try {
			String orders = bitgetService.getOrderHistory(symbol, startTime, endTime, limit);
			return ResponseEntity.ok(orders);
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body("{\"error\": \"" + e.getMessage() + "\"}");
		}
	}

	/**
	 * Endpoint genérico para hacer peticiones GET a Bitget
	 * GET /api/bitget/request?endpoint=/api/v2/spot/account/assets
	 */
	@GetMapping("/request")
	public ResponseEntity<String> makeCustomRequest(@RequestParam String endpoint) {
		try {
			String response = bitgetService.makeAuthenticatedGetRequest(endpoint);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body("{\"error\": \"" + e.getMessage() + "\"}");
		}
	}
}
