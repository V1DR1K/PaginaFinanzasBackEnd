package com.finanzas.service;

import com.finanzas.models.EventoRecordatorio;
import com.finanzas.models.Usuario;
import com.finanzas.models.TipoEvento;
import com.finanzas.models.dto.EventoRecordatorioRequest;
import com.finanzas.models.dto.EventoRecordatorioResponse;
import com.finanzas.repository.EventoRecordatorioRepository;
import com.finanzas.repository.TipoEventoRepository;
import com.finanzas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventoRecordatorioService {
    @Autowired
    private EventoRecordatorioRepository eventoRecordatorioRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired(required = false)
    private JavaMailSender mailSender;
    @Autowired
    private TipoEventoRepository tipoEventoRepository;

    public List<EventoRecordatorioResponse> getEventos(Long usuarioId) {
        return eventoRecordatorioRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(e -> new EventoRecordatorioResponse(e.getId(), e.getFecha(), e.getDescripcion(), e.getTipo().getId(), e.getTipo().getNombre()))
                .collect(Collectors.toList());
    }

    @Transactional
    public EventoRecordatorioResponse crearEvento(Long usuarioId, EventoRecordatorioRequest req) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        TipoEvento tipo = tipoEventoRepository.findById(req.getTipoId())
                .orElseThrow(() -> new IllegalArgumentException("Tipo de evento no encontrado"));
        EventoRecordatorio evento = new EventoRecordatorio();
        evento.setFecha(req.getFecha());
        evento.setDescripcion(req.getDescripcion());
        evento.setUsuario(usuario);
        evento.setEmail(usuario.getEmail());
        evento.setTipo(tipo);
        evento = eventoRecordatorioRepository.save(evento);
        return new EventoRecordatorioResponse(evento.getId(), evento.getFecha(), evento.getDescripcion(), evento.getTipo().getId(), evento.getTipo().getNombre());
    }

    public List<EventoRecordatorioResponse> getEventosFuturos(Long usuarioId) {
        LocalDate hoy = LocalDate.now();
        return eventoRecordatorioRepository.findByUsuarioId(usuarioId)
                .stream()
                .filter(e -> !e.getFecha().isBefore(hoy))
                .map(e -> new EventoRecordatorioResponse(e.getId(), e.getFecha(), e.getDescripcion(), e.getTipo().getId(), e.getTipo().getNombre()))
                .collect(Collectors.toList());
    }

    public List<EventoRecordatorioResponse> getEventosPasados(Long usuarioId) {
        LocalDate hoy = LocalDate.now();
        return eventoRecordatorioRepository.findByUsuarioId(usuarioId)
                .stream()
                .filter(e -> e.getFecha().isBefore(hoy))
                .map(e -> new EventoRecordatorioResponse(e.getId(), e.getFecha(), e.getDescripcion(), e.getTipo().getId(), e.getTipo().getNombre()))
                .collect(Collectors.toList());
    }

    // Ejecuta todos los días a las 8 AM
    @Scheduled(cron = "0 0 8 * * *")
    public void enviarRecordatoriosPendientes() {
        LocalDate manana = LocalDate.now().plusDays(1);
        List<EventoRecordatorio> eventos = eventoRecordatorioRepository.findByFecha(manana);
        for (EventoRecordatorio evento : eventos) {
            if (evento.getEmail() != null) {
                enviarEmailRecordatorio(evento.getEmail(), evento.getDescripcion(), evento.getFecha());
            }
        }
    }

    private void enviarEmailRecordatorio(String email, String descripcion, LocalDate fecha) {
        if (mailSender != null) {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setTo(email);
            mensaje.setSubject("Recordatorio de evento personal");
            mensaje.setText("Tienes un evento programado para el " + fecha + ":\n" + descripcion);
            mailSender.send(mensaje);
        }
    }
}
