package com.finanzas.service;

import com.finanzas.models.Crypto;
import com.finanzas.models.CryptoSimbolo;
import com.finanzas.models.dto.CryptoRequest;
import com.finanzas.models.dto.CryptoSymbolDTO;
import com.finanzas.repository.CryptoRepository;
import com.finanzas.repository.CryptoSimboloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CryptoService {

    @Autowired
    private CryptoRepository cryptoRepository;

    @Autowired
    private CryptoSimboloRepository cryptoSimboloRepository;

    public List<Crypto> getCryptos(Long userId) {
        return cryptoRepository.findByUserIdOrderByPercentageDesc(userId);
    }

    public List<Crypto> getCryptosActivas(Long userId) {
        return cryptoRepository.findByUserIdAndActivoOrderByPercentageDesc(userId, true);
    }

    public List<CryptoSymbolDTO> getSimbolos() {
        return cryptoSimboloRepository.findByActivoOrderByDisplayAsc(true).stream()
                .map(s -> new CryptoSymbolDTO(s.getInstId(), s.getDisplay(), s.getSource()))
                .collect(Collectors.toList());
    }

    @Transactional
    public Crypto createCrypto(Long userId, CryptoRequest request) {
        // Si ya existe, aumentamos amount; si no, creamos la entidad
        Crypto existente = cryptoRepository.findByUserIdAndInstId(userId, request.getInstId()).orElse(null);
        if (existente != null) {
            existente.setAmount(existente.getAmount().add(request.getAmount()));
            // guardar provisionalmente el amount actualizado
            cryptoRepository.save(existente);
        } else {
            Crypto crypto = new Crypto();
            crypto.setUserId(userId);
            crypto.setInstId(request.getInstId());
            crypto.setSymbol(request.getSymbol());
            crypto.setAmount(request.getAmount());
            // percentage será recalculado
            crypto.setPercentage(request.getPercentage());
            crypto.setSource(request.getSource());
            crypto.setDisplay(request.getDisplay());
            crypto.setActivo(request.getActivo() != null ? request.getActivo() : true);
            cryptoRepository.save(crypto);
        }

        // Recalcular porcentajes proporcionalmente a los amounts entre todas las cryptos activas
        List<Crypto> actives = cryptoRepository.findByUserIdAndActivoOrderByPercentageDesc(userId, true);
        // calcular suma total de amounts
        java.math.BigDecimal totalAmount = actives.stream()
                .map(Crypto::getAmount)
                .filter(a -> a != null)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        if (totalAmount.compareTo(java.math.BigDecimal.ZERO) > 0) {
            for (Crypto c : actives) {
                java.math.BigDecimal percent = c.getAmount()
                        .multiply(java.math.BigDecimal.valueOf(100))
                        .divide(totalAmount, 8, java.math.RoundingMode.HALF_UP)
                        .setScale(2, java.math.RoundingMode.HALF_UP);
                c.setPercentage(percent);
            }
            cryptoRepository.saveAll(actives);
        }

        // devolver la tenencia (actualizada)
        return cryptoRepository.findByUserIdAndInstId(userId, request.getInstId())
                .orElseThrow(() -> new IllegalArgumentException("Error al crear/actualizar la tenencia"));
    }

    @Transactional
    public Crypto updateCrypto(Long userId, Long cryptoId, CryptoRequest request) {
        // Verificar propiedad
        Crypto crypto = cryptoRepository.findByIdAndUserId(cryptoId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Crypto no encontrado o no tienes permiso"));

        // Si cambió el instId, verificar que no exista duplicado
        if (!crypto.getInstId().equals(request.getInstId())) {
            if (cryptoRepository.existsByUserIdAndInstId(userId, request.getInstId())) {
                throw new IllegalArgumentException("Ya existe una tenencia de " + request.getInstId());
            }
        }

        // Validar porcentaje total
        BigDecimal totalPercentage = cryptoRepository.sumPercentageByUserId(userId);
        if (totalPercentage != null) {
            // Restar el porcentaje actual y sumar el nuevo
            BigDecimal nuevoTotal = totalPercentage.subtract(crypto.getPercentage()).add(request.getPercentage());
            if (nuevoTotal.compareTo(BigDecimal.valueOf(100)) > 0) {
                System.out.println("WARNING: El porcentaje total supera 100% para el usuario " + userId);
            }
        }

        // Actualizar
        crypto.setInstId(request.getInstId());
        crypto.setSymbol(request.getSymbol());
        crypto.setAmount(request.getAmount());
        crypto.setPercentage(request.getPercentage());
        crypto.setSource(request.getSource());
        crypto.setDisplay(request.getDisplay());
        if (request.getActivo() != null) {
            crypto.setActivo(request.getActivo());
        }

        return cryptoRepository.save(crypto);
    }

    @Transactional
    public void deleteCrypto(Long userId, Long cryptoId) {
        // Verificar propiedad
        Crypto crypto = cryptoRepository.findByIdAndUserId(cryptoId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Crypto no encontrado o no tienes permiso"));

        cryptoRepository.delete(crypto);
    }

    @Transactional
    public Crypto toggleActivo(Long userId, Long cryptoId) {
        // Verificar propiedad
        Crypto crypto = cryptoRepository.findByIdAndUserId(cryptoId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Crypto no encontrado o no tienes permiso"));

        crypto.setActivo(!crypto.getActivo());
        return cryptoRepository.save(crypto);
    }
}
