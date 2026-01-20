package com.finanzas.service;

import com.finanzas.models.Adjunto;
import com.finanzas.models.Movimientos;
import com.finanzas.repository.AdjuntoRepository;
import com.finanzas.repository.MovimientosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AdjuntoService {

    @Autowired
    private AdjuntoRepository adjuntoRepository;

    @Autowired
    private MovimientosRepository movimientosRepository;

    @Value("${upload.directory:uploads}")
    private String uploadDirectory;

    @Value("${upload.max-file-size:10485760}") // 10MB por defecto
    private long maxFileSize;

    private static final List<String> ALLOWED_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/webp",
            "application/pdf",
            "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    );

    public List<Adjunto> findByMovimientoIdAndUserId(Long movimientoId, Long userId) {
        return adjuntoRepository.findByMovimientoIdAndUserId(movimientoId, userId);
    }

    public Optional<Adjunto> findByIdAndUserId(Long adjuntoId, Long userId) {
        return adjuntoRepository.findByIdAndUserId(adjuntoId, userId);
    }

    @Transactional
    public Adjunto uploadAdjunto(Long movimientoId, MultipartFile file, Long userId) throws IOException {
        // Verificar que el movimiento pertenece al usuario
        Movimientos movimiento = movimientosRepository.findByIdAndUserId(movimientoId.intValue(), userId)
                .orElseThrow(() -> new IllegalArgumentException("Movimiento no encontrado"));

        // Validar archivo
        validateFile(file);

        // Convertir archivo a Base64
        byte[] fileBytes = file.getBytes();
        String base64Content = Base64.getEncoder().encodeToString(fileBytes);

        // Crear registro en BD
        Adjunto adjunto = new Adjunto();
        adjunto.setMovimientoId(movimientoId);
        adjunto.setNombreArchivo(file.getOriginalFilename());
        adjunto.setTipoArchivo(file.getContentType());
        adjunto.setTamano(file.getSize());
        adjunto.setBase64(base64Content); // Nuevo campo en la entidad Adjunto
        // El campo url ya no se usa para almacenamiento físico

        return adjuntoRepository.save(adjunto);
    }

    @Transactional
    public void deleteAdjunto(Long adjuntoId, Long userId) throws IOException {
        Adjunto adjunto = adjuntoRepository.findByIdAndUserId(adjuntoId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Adjunto no encontrado"));

        // Eliminar registro de BD
        adjuntoRepository.delete(adjunto);
    }

    public byte[] getAdjuntoFile(Long adjuntoId, Long userId) {
        Adjunto adjunto = adjuntoRepository.findByIdAndUserId(adjuntoId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Adjunto no encontrado"));
        return Base64.getDecoder().decode(adjunto.getBase64());
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("El archivo está vacío");
        }

        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException("El archivo excede el tamaño máximo permitido");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Tipo de archivo no permitido. Tipos permitidos: imágenes, PDF, Word, Excel");
        }
    }
}
