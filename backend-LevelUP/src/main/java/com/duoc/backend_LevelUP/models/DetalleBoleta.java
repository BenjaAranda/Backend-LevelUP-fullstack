package com.duoc.backend_LevelUP.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "detalle_boleta")
public class DetalleBoleta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer cantidad;

    private Integer precioUnitario; // Precio al momento de la compra

    @ManyToOne
    @JoinColumn(name = "id_boleta")
    private Boleta boleta;

    @ManyToOne
    @JoinColumn(name = "id_producto")
    private Producto producto;
}