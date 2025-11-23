package com.duoc.backend_LevelUP.repositories;

import com.duoc.backend_LevelUP.models.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    Optional<Producto> findByCodigo(String codigo);
}