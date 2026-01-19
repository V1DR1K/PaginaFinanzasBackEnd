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
            ResponseEntity<Map> resp = restTemplate.getForEntity(DOLAR_OFICIAL_URL, Map.class);
            if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
                throw new RuntimeException("La API externa devolvió un estado no exitoso");
            }

            Map<String,Object> body = resp.getBody();
            // El JSON de dolarapi puede devolver un array u objeto; manejar caso objeto con key 'compra'/'venta' o map anidado
            // Ejemplo esperado: { "compra": 150.0, "venta": 155.0 }

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
            ResponseEntity<List> resp = restTemplate.getForEntity(DOLAR_TODOS_URL, List.class);
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
                // Usar nombre como campo principal (preservar original para la respuesta)
                Object nombreObj = item.get("nombre");
                String nombreOriginal = nombreObj != null ? nombreObj.toString() : null;
                String nombreLower = nombreOriginal != null ? nombreOriginal.toLowerCase() : null;

                // Si no hay 'nombre', intentar 'tipo' como respaldo
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

                if (!include) continue; // saltar si no es de interés

                Object compra = item.get("compra");
                Object venta = item.get("venta");
                Object fecha = item.get("fecha");

                DolarTipoDTO dto = new DolarTipoDTO();
                // Devolver el nombre original (no en lowercase) para mostrar en frontend
                dto.setNombre(nombreOriginal != null ? nombreOriginal : (nombreLower != null ? nombreLower : null));
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
