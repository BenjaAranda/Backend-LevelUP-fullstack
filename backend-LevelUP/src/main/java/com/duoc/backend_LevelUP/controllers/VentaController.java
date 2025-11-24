package com.duoc.backend_LevelUP.controllers;

import com.duoc.backend_LevelUP.dto.VentaRequest;
import com.duoc.backend_LevelUP.models.Boleta;
import com.duoc.backend_LevelUP.models.DetalleBoleta;
import com.duoc.backend_LevelUP.models.Producto;
import com.duoc.backend_LevelUP.models.Usuario;
import com.duoc.backend_LevelUP.repositories.BoletaRepository;
import com.duoc.backend_LevelUP.repositories.ProductoRepository;
import com.duoc.backend_LevelUP.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/ventas")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class VentaController {

    private final BoletaRepository boletaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;

    @PostMapping
    @Transactional // Si algo falla, se revierte todo (stock y boleta)
    public ResponseEntity<?> crearVenta(@RequestBody VentaRequest request) {

        // 1. Buscar usuario (Opcional: si es invitado, manejar lógica aparte)
        Usuario usuario = usuarioRepository.findByEmail(request.getUsuarioEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. Crear Boleta
        Boleta boleta = Boleta.builder()
                .fecha(LocalDateTime.now())
                .total(request.getTotalVenta())
                .usuario(usuario)
                .build();

        // 3. Procesar Items y Descontar Stock
        List<DetalleBoleta> detalles = new ArrayList<>();

        for (var item : request.getItems()) {
            Producto producto = productoRepository.findByCodigo(item.getProductoCodigo())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + item.getProductoCodigo()));

            if (producto.getStock() < item.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para: " + producto.getNombre());
            }

            // Descontar stock
            producto.setStock(producto.getStock() - item.getCantidad());
            productoRepository.save(producto);

            // Crear detalle
            detalles.add(DetalleBoleta.builder()
                    .boleta(boleta)
                    .producto(producto)
                    .cantidad(item.getCantidad())
                    .precioUnitario(item.getPrecioUnitario())
                    .build());
        }

        boleta.setDetalles(detalles);
        Boleta boletaGuardada = boletaRepository.save(boleta);

        return ResponseEntity.ok(boletaGuardada);
    }

    @GetMapping("/mis-compras")
    public ResponseEntity<List<Boleta>> getMisCompras(@RequestParam String email) {
        return ResponseEntity.ok(boletaRepository.findByUsuarioEmail(email));
    }
}