package com.finanzas.controller;

import com.finanzas.config.CustomUserDetails;
import com.finanzas.models.Crypto;
import com.finanzas.models.dto.CryptoRequest;
import com.finanzas.models.dto.CryptoSymbolDTO;
import com.finanzas.service.CryptoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cryptos")
public class CryptoController {

    @Autowired
    private CryptoService cryptoService;

    @GetMapping
    public ResponseEntity<List<Crypto>> getCryptos(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        List<Crypto> cryptos = cryptoService.getCryptos(userId);
        return ResponseEntity.ok(cryptos);
    }

    @GetMapping("/activas")
    public ResponseEntity<List<Crypto>> getCryptosActivas(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        List<Crypto> cryptos = cryptoService.getCryptosActivas(userId);
        return ResponseEntity.ok(cryptos);
    }

    @GetMapping("/simbolos")
    public ResponseEntity<List<CryptoSymbolDTO>> getSimbolos() {
        List<CryptoSymbolDTO> simbolos = cryptoService.getSimbolos();
        return ResponseEntity.ok(simbolos);
    }

    @PostMapping
    public ResponseEntity<?> createCrypto(
            @Valid @RequestBody CryptoRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            Long userId = userDetails.getUserId();
            Crypto crypto = cryptoService.createCrypto(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(crypto);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> updateCrypto(
            @PathVariable Long id,
            @Valid @RequestBody CryptoRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            Long userId = userDetails.getUserId();
            Crypto crypto = cryptoService.updateCrypto(userId, id, request);
            return ResponseEntity.ok(crypto);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCrypto(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            Long userId = userDetails.getUserId();
            cryptoService.deleteCrypto(userId, id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/{id}/toggle")
    public ResponseEntity<?> toggleActivo(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            Long userId = userDetails.getUserId();
            Crypto crypto = cryptoService.toggleActivo(userId, id);
            return ResponseEntity.ok(crypto);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
