package com.duoc.backend_LevelUP.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequest {
    private String productoCodigo;
    private Integer cantidad;
    private Integer precioUnitario;
}