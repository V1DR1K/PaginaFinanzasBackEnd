package com.finanzas.service;

import com.finanzas.models.Categoria;
import com.finanzas.models.dto.CategoriaRequest;
import com.finanzas.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    public List<Categoria> findAllByUserId(Long userId) {
        return categoriaRepository.findRootCategoriesByUserId(userId);
    }

    public List<Categoria> findByUserIdAndTipo(Long userId, String tipo) {
        return categoriaRepository.findByUserIdAndTipo(userId, tipo);
    }

    public Optional<Categoria> findByIdAndUserId(Long id, Long userId) {
        return categoriaRepository.findByIdAndUserId(id, userId);
    }

    @Transactional
    public Categoria create(CategoriaRequest request, Long userId) {
        // Validar unicidad
        if (categoriaRepository.existsByNombreAndTipoAndUserIdAndCategoriaPadreId(
                request.getNombre(), request.getTipo(), userId, request.getCategoriaPadreId())) {
            throw new IllegalArgumentException("Ya existe una categoría con este nombre");
        }

        // Validar categoría padre si existe
        if (request.getCategoriaPadreId() != null) {
            Categoria padre = categoriaRepository.findByIdAndUserId(request.getCategoriaPadreId(), userId)
                    .orElseThrow(() -> new IllegalArgumentException("Categoría padre no encontrada"));

            if (!padre.getTipo().equals(request.getTipo())) {
                throw new IllegalArgumentException("El tipo debe coincidir con la categoría padre");
            }
        }

        Categoria categoria = new Categoria();
        categoria.setNombre(request.getNombre());
        categoria.setTipo(request.getTipo());
        categoria.setIcono(request.getIcono());
        categoria.setColor(request.getColor());
        categoria.setUserId(userId);
        categoria.setCategoriaPadreId(request.getCategoriaPadreId());

        return categoriaRepository.save(categoria);
    }

    @Transactional
    public Categoria update(Long id, CategoriaRequest request, Long userId) {
        Categoria categoria = categoriaRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

        // Validar categoría padre si cambió
        if (request.getCategoriaPadreId() != null && !request.getCategoriaPadreId().equals(categoria.getCategoriaPadreId())) {
            Categoria padre = categoriaRepository.findByIdAndUserId(request.getCategoriaPadreId(), userId)
                    .orElseThrow(() -> new IllegalArgumentException("Categoría padre no encontrada"));

            if (!padre.getTipo().equals(request.getTipo())) {
                throw new IllegalArgumentException("El tipo debe coincidir con la categoría padre");
            }
        }

        categoria.setNombre(request.getNombre());
        categoria.setTipo(request.getTipo());
        categoria.setIcono(request.getIcono());
        categoria.setColor(request.getColor());
        categoria.setCategoriaPadreId(request.getCategoriaPadreId());

        return categoriaRepository.save(categoria);
    }

    @Transactional
    public void delete(Long id, Long userId) {
        Categoria categoria = categoriaRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

        categoriaRepository.delete(categoria);
    }
}
