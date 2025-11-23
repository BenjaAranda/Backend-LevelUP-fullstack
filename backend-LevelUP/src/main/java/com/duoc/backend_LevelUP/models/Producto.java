package com.duoc.backend_LevelUP.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "producto")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String codigo; // Ej: "JDM001" (Vital para tu frontend)

    @Column(nullable = false)
    private String nombre;

    private String img; // Ruta de la imagen (ej: "/catan.png")

    private Integer precio;

    private Integer stock;

    @Column(length = 1000) // Permitir descripciones largas
    private String descripcion;

    // Relación con Categoría (Requisito Anexo 1)
    @ManyToOne
    @JoinColumn(name = "id_categoria")
    private Categoria categoria;
}
