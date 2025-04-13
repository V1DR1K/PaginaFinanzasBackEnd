package com.finanzas.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.finanzas.models.Movimientos;

public interface MovimientosRepository extends JpaRepository<Movimientos, Integer> {

	@Query("SELECT m FROM Movimientos m WHERE m.fecha LIKE CONCAT(:mes, '%')")
	List<Movimientos> findAllByMes(@Param("mes") String mes);

}
