package com.finanzas.repository;

import com.finanzas.models.CryptoSimbolo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CryptoSimboloRepository extends JpaRepository<CryptoSimbolo, Long> {

    List<CryptoSimbolo> findByActivoOrderByDisplayAsc(Boolean activo);

    Optional<CryptoSimbolo> findByInstId(String instId);
}
