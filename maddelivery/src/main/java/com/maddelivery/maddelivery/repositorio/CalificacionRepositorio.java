package com.maddelivery.maddelivery.repositorio;

import com.maddelivery.maddelivery.entidad.Calificacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CalificacionRepositorio extends JpaRepository<Calificacion, Long> {

    // ❌ DEPRECATED - No usar para admin panel con muchos datos
    List<Calificacion> findAllByOrderByFechaCreacionDesc();

    // Método para buscar por puntuación
    List<Calificacion> findByPuntuacion(Integer puntuacion);

    // ✅ NUEVO - Con paginación (usa el método por defecto de JpaRepository)
    // Spring Data JPA ya proporciona findAll(Pageable) automáticamente
    // Solo necesitamos usarlo en el controller con Sort
}