package com.finanzas.controller;

import com.finanzas.models.dto.EventoRecordatorioRequest;
import com.finanzas.models.dto.EventoRecordatorioResponse;
import com.finanzas.service.EventoRecordatorioService;
import com.finanzas.config.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/eventos")
public class EventoRecordatorioController {
    @Autowired
    private EventoRecordatorioService eventoRecordatorioService;

    @GetMapping
    public ResponseEntity<List<EventoRecordatorioResponse>> getEventos(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        return ResponseEntity.ok(eventoRecordatorioService.getEventos(userId));
    }

    @PostMapping
    public ResponseEntity<EventoRecordatorioResponse> crearEvento(@Valid @RequestBody EventoRecordatorioRequest req,
                                                                 @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        EventoRecordatorioResponse response = eventoRecordatorioService.crearEvento(userId, req);
        return ResponseEntity.ok(response);
    }
}
