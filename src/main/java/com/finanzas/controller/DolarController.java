package com.finanzas.controller;

import com.finanzas.models.dto.DolarResponseDTO;
import com.finanzas.service.DolarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dolar")
public class DolarController {

    @Autowired
    private DolarService dolarService;

    @GetMapping("/oficial")
    public ResponseEntity<?> getDolarOficial() {
        try {
            DolarResponseDTO dto = dolarService.getDolarOficial();
            System.out.print(dto);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(java.util.Collections.singletonMap("error", ex.getMessage()));
        }
    }

    @GetMapping("/todos")
    public ResponseEntity<?> getAllDolares() {
        try {
            java.util.List<com.finanzas.models.dto.DolarTipoDTO> lista = dolarService.getAllDolares();
            return ResponseEntity.ok(lista);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(java.util.Collections.singletonMap("error", ex.getMessage()));
        }
    }
}
