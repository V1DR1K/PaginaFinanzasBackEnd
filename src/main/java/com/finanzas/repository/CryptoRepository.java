package com.finanzas.repository;

import com.finanzas.models.Crypto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CryptoRepository extends JpaRepository<Crypto, Long> {

    List<Crypto> findByUserIdOrderByPercentageDesc(Long userId);

    List<Crypto> findByUserIdAndActivoOrderByPercentageDesc(Long userId, Boolean activo);

    Optional<Crypto> findByIdAndUserId(Long id, Long userId);

    Optional<Crypto> findByUserIdAndInstId(Long userId, String instId);

    boolean existsByUserIdAndInstId(Long userId, String instId);

    @Query("SELECT SUM(c.percentage) FROM Crypto c WHERE c.userId = :userId AND c.activo = true")
    BigDecimal sumPercentageByUserId(@Param("userId") Long userId);
}
