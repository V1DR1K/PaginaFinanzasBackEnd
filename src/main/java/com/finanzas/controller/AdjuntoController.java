package com.finanzas.controller;

import com.finanzas.config.CustomUserDetails;
import com.finanzas.models.Adjunto;
import com.finanzas.service.AdjuntoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/movimiento/{movimientoId}/adjuntos")
public class AdjuntoController {

    @Autowired
    private AdjuntoService adjuntoService;

    @GetMapping
    public ResponseEntity<List<Adjunto>> getAdjuntos(
            @PathVariable Long movimientoId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        List<Adjunto> adjuntos = adjuntoService.findByMovimientoIdAndUserId(movimientoId, userId);
        return ResponseEntity.ok(adjuntos);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadAdjunto(
            @PathVariable Long movimientoId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            Long userId = userDetails.getUserId();
            Adjunto adjunto = adjuntoService.uploadAdjunto(movimientoId, file, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Archivo subido exitosamente");
            response.put("adjunto", adjunto);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (IOException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al guardar el archivo: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/{adjuntoId}/download")
    public ResponseEntity<Resource> downloadAdjunto(
            @PathVariable Long movimientoId,
            @PathVariable Long adjuntoId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            Long userId = userDetails.getUserId();

            Adjunto adjunto = adjuntoService.findByIdAndUserId(adjuntoId, userId)
                    .orElseThrow(() -> new IllegalArgumentException("Adjunto no encontrado"));

            Path filePath = adjuntoService.getFilePath(adjuntoId, userId);
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(adjunto.getTipoArchivo()))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + adjunto.getNombreArchivo() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{adjuntoId}")
    public ResponseEntity<?> deleteAdjunto(
            @PathVariable Long movimientoId,
            @PathVariable Long adjuntoId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            Long userId = userDetails.getUserId();
            adjuntoService.deleteAdjunto(adjuntoId, userId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (IOException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al eliminar el archivo: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
