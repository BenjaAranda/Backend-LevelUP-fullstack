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
    private List<ItemRequest> items;
    // Puedes agregar dirección aquí si lo necesitas guardar en la boleta
}