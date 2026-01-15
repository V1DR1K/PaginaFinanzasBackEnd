package com.finanzas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.finanzas.models.Movimientos;

public interface MovimientosRepository extends JpaRepository<Movimientos, Integer> {



}
