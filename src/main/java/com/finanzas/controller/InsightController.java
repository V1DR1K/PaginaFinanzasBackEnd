package com.finanzas.controller;

import com.finanzas.config.CustomUserDetails;
import com.finanzas.models.Insight;
import com.finanzas.service.InsightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/insights")
public class InsightController {

    @Autowired
    private InsightService insightService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getInsights(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        Map<String, Object> insights = insightService.getInsights(userId);
        return ResponseEntity.ok(insights);
    }

    @PostMapping("/{id}/leer")
    public ResponseEntity<?> marcarComoLeido(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            Long userId = userDetails.getUserId();
            Insight insight = insightService.marcarComoLeido(id, userId);
            return ResponseEntity.ok(insight);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/generar")
    public ResponseEntity<?> generarInsightsManual(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            Long userId = userDetails.getUserId();
            insightService.generarInsights(userId);

            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Insights generados exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al generar insights: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
}
