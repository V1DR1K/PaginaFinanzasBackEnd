package com.finanzas.service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.finanzas.config.BitgetConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BitgetService {

	private final BitgetConfig bitgetConfig;
	private final RestTemplate restTemplate = new RestTemplate();

	/**
	 * Genera la firma HMAC SHA256 requerida por Bitget
	 */
	private String generateSignature(String timestamp, String method, String requestPath, String body) {
		try {
			String preHash = timestamp + method.toUpperCase() + requestPath + (body != null ? body : "");
			Mac mac = Mac.getInstance("HmacSHA256");
			SecretKeySpec secretKeySpec = new SecretKeySpec(bitgetConfig.getSecret().getBytes(StandardCharsets.UTF_8),
					"HmacSHA256");
			mac.init(secretKeySpec);
			byte[] hash = mac.doFinal(preHash.getBytes(StandardCharsets.UTF_8));
			return Base64.getEncoder().encodeToString(hash);
		} catch (Exception e) {
			log.error("Error generando firma: ", e);
			throw new RuntimeException("Error al generar firma", e);
		}
	}

	/**
	 * Crea los headers necesarios para la autenticación con Bitget
	 */
	private HttpHeaders createAuthHeaders(String method, String requestPath, String body) {
		String timestamp = String.valueOf(Instant.now().toEpochMilli());
		String signature = generateSignature(timestamp, method, requestPath, body);

		HttpHeaders headers = new HttpHeaders();
		headers.set("ACCESS-KEY", bitgetConfig.getKey());
		headers.set("ACCESS-SIGN", signature);
		headers.set("ACCESS-TIMESTAMP", timestamp);
		headers.set("ACCESS-PASSPHRASE", bitgetConfig.getPassphrase());
		headers.set("Content-Type", "application/json");
		headers.set("locale", "es-ES");

		return headers;
	}

	/**
	 * Obtiene el balance de la cuenta
	 */
	public String getAccountBalance() {
		String endpoint = "/api/v2/spot/account/assets";
		String url = bitgetConfig.getUrl() + endpoint;

		HttpHeaders headers = createAuthHeaders("GET", endpoint, "");
		HttpEntity<String> entity = new HttpEntity<>(headers);

		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
			return response.getBody();
		} catch (Exception e) {
			log.error("Error obteniendo balance: ", e);
			throw new RuntimeException("Error al obtener balance de Bitget", e);
		}
	}

	/**
	 * Obtiene información de la cuenta
	 */
	public String getAccountInfo() {
		String endpoint = "/api/v2/spot/account/info";
		String url = bitgetConfig.getUrl() + endpoint;

		HttpHeaders headers = createAuthHeaders("GET", endpoint, "");
		HttpEntity<String> entity = new HttpEntity<>(headers);

		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
			return response.getBody();
		} catch (Exception e) {
			log.error("Error obteniendo info de cuenta: ", e);
			throw new RuntimeException("Error al obtener info de cuenta", e);
		}
	}

	/**
	 * Obtiene el historial de órdenes
	 */
	public String getOrderHistory(String symbol, String startTime, String endTime, int limit) {
		String endpoint = "/api/v2/spot/trade/history-orders";
		String queryParams = String.format("?symbol=%s&startTime=%s&endTime=%s&limit=%d", symbol, startTime, endTime,
				limit);
		String fullEndpoint = endpoint + queryParams;
		String url = bitgetConfig.getUrl() + fullEndpoint;

		HttpHeaders headers = createAuthHeaders("GET", fullEndpoint, "");
		HttpEntity<String> entity = new HttpEntity<>(headers);

		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
			return response.getBody();
		} catch (Exception e) {
			log.error("Error obteniendo historial de órdenes: ", e);
			throw new RuntimeException("Error al obtener historial de órdenes", e);
		}
	}

	/**
	 * Realiza una petición GET genérica a Bitget con autenticación
	 */
	public String makeAuthenticatedGetRequest(String endpoint) {
		String url = bitgetConfig.getUrl() + endpoint;

		HttpHeaders headers = createAuthHeaders("GET", endpoint, "");
		HttpEntity<String> entity = new HttpEntity<>(headers);

		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
			return response.getBody();
		} catch (Exception e) {
			log.error("Error en petición GET a {}: ", endpoint, e);
			throw new RuntimeException("Error en petición a Bitget: " + endpoint, e);
		}
	}

	/**
	 * Realiza una petición POST genérica a Bitget con autenticación
	 */
	public String makeAuthenticatedPostRequest(String endpoint, String body) {
		String url = bitgetConfig.getUrl() + endpoint;

		HttpHeaders headers = createAuthHeaders("POST", endpoint, body);
		HttpEntity<String> entity = new HttpEntity<>(body, headers);

		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
			return response.getBody();
		} catch (Exception e) {
			log.error("Error en petición POST a {}: ", endpoint, e);
			throw new RuntimeException("Error en petición a Bitget: " + endpoint, e);
		}
	}
}
