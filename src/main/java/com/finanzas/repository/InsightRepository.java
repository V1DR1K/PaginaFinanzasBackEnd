package com.finanzas.repository;

import com.finanzas.models.Insight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InsightRepository extends JpaRepository<Insight, Long> {

    List<Insight> findByUserIdOrderByFechaDesc(Long userId);

    List<Insight> findByUserIdAndLeidoFalseOrderByFechaDesc(Long userId);

    long countByUserIdAndLeidoFalse(Long userId);
}
