package com.duoc.backend_LevelUP.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VentaRequest {
    private String usuarioEmail;
    private Integer totalVenta;

    // --- NUEVO: Datos de Envío ---
    private String calle;
    private String numero;
    private String departamento; // Opcional
    private String comuna;
    private String region;
    private String telefono;

    private List<ItemRequest> items;
}