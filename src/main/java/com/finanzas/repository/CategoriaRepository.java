package com.finanzas.repository;

import com.finanzas.models.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    List<Categoria> findByUserIdAndCategoriaPadreIdIsNull(Long userId);

    List<Categoria> findByUserIdAndTipo(Long userId, String tipo);

    Optional<Categoria> findByIdAndUserId(Long id, Long userId);

    List<Categoria> findByCategoriaPadreId(Long categoriaPadreId);

    boolean existsByNombreAndTipoAndUserIdAndCategoriaPadreId(
        String nombre, String tipo, Long userId, Long categoriaPadreId);

    @Query("SELECT c FROM Categoria c WHERE c.userId = :userId AND c.categoriaPadreId IS NULL ORDER BY c.nombre ASC")
    List<Categoria> findRootCategoriesByUserId(@Param("userId") Long userId);
}
