package com.duoc.backend_LevelUP.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "boleta")
public class Boleta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fecha;

    @Column(nullable = false)
    private Integer total;

    @Enumerated(EnumType.STRING)
    private EstadoPedido estado;

    // Datos de Envío
    private String direccion;
    private String comuna;
    private String region;
    private String telefonoContacto;

    // --- CORRECCIÓN DE REDUNDANCIA ---
    // Usamos explícitamente "usuario_id" como la única columna para la llave
    // foránea.
    @ManyToOne(fetch = FetchType.EAGER) // Eager para que cargue el usuario al pedir la boleta
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "boleta", cascade = CascadeType.ALL)
    private List<DetalleBoleta> detalles;

    public enum EstadoPedido {
        PENDIENTE,
        EN_PREPARACION,
        ENVIADO,
        ENTREGADO
    }
}