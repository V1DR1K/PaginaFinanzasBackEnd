package com.finanzas.controller;

import com.finanzas.models.dto.TipoEventoRequest;
import com.finanzas.models.dto.TipoEventoResponse;
import com.finanzas.service.TipoEventoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tipos-evento")
public class TipoEventoController {
    @Autowired
    private TipoEventoService tipoEventoService;

    @GetMapping("/getTipos")
    public ResponseEntity<List<TipoEventoResponse>> getTiposEvento() {
        return ResponseEntity.ok(tipoEventoService.getTiposEvento());
    }

    @PostMapping("/addTipos")
    public ResponseEntity<TipoEventoResponse> crearTipoEvento(@Valid @RequestBody TipoEventoRequest req) {
        TipoEventoResponse response = tipoEventoService.crearTipoEvento(req);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/updateTipo/{id}")
    public ResponseEntity<TipoEventoResponse> updateTipoEvento(@PathVariable Long id, @Valid @RequestBody TipoEventoRequest req) {
        TipoEventoResponse response = tipoEventoService.updateTipoEvento(id, req);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/deleteTipo/{id}")
    public ResponseEntity<Void> deleteTipoEvento(@PathVariable Long id) {
        tipoEventoService.deleteTipoEvento(id);
        return ResponseEntity.noContent().build();
    }
}
