package com.duoc.backend_LevelUP.repositories;

import com.duoc.backend_LevelUP.models.Boleta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BoletaRepository extends JpaRepository<Boleta, Long> {
    List<Boleta> findByUsuarioEmail(String email);
}