package com.finanzas.service;

import com.finanzas.models.TipoEvento;
import com.finanzas.models.dto.TipoEventoRequest;
import com.finanzas.models.dto.TipoEventoResponse;
import com.finanzas.repository.TipoEventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TipoEventoService {
    @Autowired
    private TipoEventoRepository tipoEventoRepository;

    public List<TipoEventoResponse> getTiposEvento() {
        return tipoEventoRepository.findAll()
                .stream()
                .map(t -> new TipoEventoResponse(t.getId(), t.getNombre(), t.getDescripcion(), t.getColor()))
                .collect(Collectors.toList());
    }

    public TipoEventoResponse crearTipoEvento(TipoEventoRequest req) {
        TipoEvento tipo = new TipoEvento();
        tipo.setNombre(req.getNombre());
        tipo.setDescripcion(req.getDescripcion());
        tipo.setColor(req.getColor());
        tipo = tipoEventoRepository.save(tipo);
        return new TipoEventoResponse(tipo.getId(), tipo.getNombre(), tipo.getDescripcion(), tipo.getColor());
    }

    public TipoEventoResponse updateTipoEvento(Long id, TipoEventoRequest req) {
        TipoEvento tipo = tipoEventoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de evento no encontrado"));
        tipo.setNombre(req.getNombre());
        tipo.setDescripcion(req.getDescripcion());
        tipo.setColor(req.getColor());
        tipo = tipoEventoRepository.save(tipo);
        return new TipoEventoResponse(tipo.getId(), tipo.getNombre(), tipo.getDescripcion(), tipo.getColor());
    }

    public void deleteTipoEvento(Long id) {
        TipoEvento tipo = tipoEventoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de evento no encontrado"));
        tipoEventoRepository.delete(tipo);
    }
}
