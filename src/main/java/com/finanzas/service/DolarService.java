package com.finanzas.service;

import com.finanzas.models.dto.DolarResponseDTO;
import com.finanzas.models.dto.DolarTipoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DolarService {

    private static final String DOLAR_OFICIAL_URL = "https://dolarapi.com/v1/dolares/oficial";
    private static final String DOLAR_TODOS_URL = "https://dolarapi.com/v1/dolares";

    @Autowired
    private RestTemplate restTemplate;

    @SuppressWarnings("unchecked")
    public DolarResponseDTO getDolarOficial() {
        try {
            ResponseEntity<Map<String, Object>> resp = restTemplate.getForEntity(DOLAR_OFICIAL_URL, (Class<Map<String, Object>>) (Class<?>) Map.class);
            if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
                throw new RuntimeException("La API externa devolvió un estado no exitoso");
            }

            Map<String,Object> body = resp.getBody();
            Object compraObj = body.get("compra");
            Object ventaObj = body.get("venta");
            Object fechaObj = body.get("fecha");

            DolarResponseDTO dto = new DolarResponseDTO();
            if (compraObj != null) dto.setCompra(Float.parseFloat(compraObj.toString()));
            if (ventaObj != null) dto.setVenta(Float.parseFloat(ventaObj.toString()));
            if (fechaObj != null) dto.setFecha(fechaObj.toString());

            return dto;
        } catch (RestClientException ex) {
            throw new RuntimeException("La API externa no respondió a tiempo o devolvió un error: " + ex.getMessage(), ex);
        } catch (Exception ex) {
            throw new RuntimeException("Error procesando la respuesta de la API externa: " + ex.getMessage(), ex);
        }
    }

    @SuppressWarnings("unchecked")
    public List<DolarTipoDTO> getAllDolares() {
        try {
            ResponseEntity<List<Map<String, Object>>> resp = restTemplate.getForEntity(DOLAR_TODOS_URL, (Class<List<Map<String, Object>>>) (Class<?>) List.class);
            if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
                throw new RuntimeException("La API externa devolvió un estado no exitoso");
            }

            List<Map<String,Object>> list = resp.getBody();
            List<DolarTipoDTO> result = new ArrayList<>();

            // Palabras clave aceptadas (lowercase)
            java.util.Set<String> keywords = new java.util.HashSet<>();
            keywords.add("oficial");
            keywords.add("blue");
            keywords.add("tarjeta");
            keywords.add("cripto");
            keywords.add("criptomoneda");

            for (Map<String,Object> item : list) {
                Object nombreObj = item.get("nombre");
                String nombreOriginal = nombreObj != null ? nombreObj.toString() : null;
                String nombreLower = nombreOriginal != null ? nombreOriginal.toLowerCase() : null;
                if (nombreLower == null) {
                    Object alt = item.get("tipo");
                    nombreOriginal = alt != null ? alt.toString() : null;
                    nombreLower = nombreOriginal != null ? nombreOriginal.toLowerCase() : null;
                }
                boolean include = false;
                if (nombreLower != null) {
                    for (String k : keywords) {
                        if (nombreLower.contains(k)) {
                            include = true;
                            break;
                        }
                    }
                }
                if (!include) continue;
                Object compra = item.get("compra");
                Object venta = item.get("venta");
                Object fecha = item.get("fecha");
                DolarTipoDTO dto = new DolarTipoDTO();
                dto.setNombre(nombreOriginal); // nombreOriginal siempre es no null en este punto
                if (compra != null) dto.setCompra(Float.parseFloat(compra.toString()));
                if (venta != null) dto.setVenta(Float.parseFloat(venta.toString()));
                if (fecha != null) dto.setFecha(fecha.toString());
                result.add(dto);
            }
            return result;
        } catch (RestClientException ex) {
            throw new RuntimeException("La API externa no respondió a tiempo o devolvió un error: " + ex.getMessage(), ex);
        } catch (Exception ex) {
            throw new RuntimeException("Error procesando la respuesta de la API externa: " + ex.getMessage(), ex);
        }
    }
}
