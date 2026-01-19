package com.finanzas.models.dto;

import java.time.LocalDate;

public class EventoRecordatorioResponse {
    private Long id;
    private LocalDate fecha;
    private String descripcion;
    private Long tipoId;
    private String tipoNombre;

    public EventoRecordatorioResponse() {}
    public EventoRecordatorioResponse(Long id, LocalDate fecha, String descripcion, Long tipoId, String tipoNombre) {
        this.id = id;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.tipoId = tipoId;
        this.tipoNombre = tipoNombre;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Long getTipoId() { return tipoId; }
    public void setTipoId(Long tipoId) { this.tipoId = tipoId; }
    public String getTipoNombre() { return tipoNombre; }
    public void setTipoNombre(String tipoNombre) { this.tipoNombre = tipoNombre; }
}
