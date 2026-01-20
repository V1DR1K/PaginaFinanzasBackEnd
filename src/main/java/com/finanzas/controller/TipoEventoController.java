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
}
