package com.duoc.backend_LevelUP.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "detalle_boleta")
public class DetalleBoleta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer cantidad;
    private Integer precioUnitario;

    // --- CORRECCIÓN: Única columna "producto_id" ---
    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    // --- CORRECCIÓN: Única columna "boleta_id" ---
    @ManyToOne
    @JoinColumn(name = "boleta_id", nullable = false)
    @JsonIgnore // Evitamos ciclo infinito al convertir a JSON
    private Boleta boleta;
}