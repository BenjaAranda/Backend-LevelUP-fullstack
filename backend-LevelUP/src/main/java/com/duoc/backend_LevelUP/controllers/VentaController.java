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

    // --- 1. CREAR VENTA (CHECKOUT) ---
    @PostMapping
    @Transactional // Importante: Si falla algo, no se guarda nada ni se descuenta stock
    public ResponseEntity<?> crearVenta(@RequestBody VentaRequest request) {

        // A. Validar Usuario
        Usuario usuario = usuarioRepository.findByEmail(request.getUsuarioEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + request.getUsuarioEmail()));

        // B. Crear Cabecera de Boleta
        Boleta boleta = Boleta.builder()
                .fecha(LocalDateTime.now())
                .total(0) // Lo calcularemos real abajo para seguridad
                .usuario(usuario)
                .detalles(new ArrayList<>()) // Lista vacía inicial
                .build();

        // C. Procesar Items
        int totalCalculado = 0;

        for (var item : request.getItems()) {
            // Buscar producto real en BD
            Producto producto = productoRepository.findByCodigo(item.getProductoCodigo())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + item.getProductoCodigo()));

            // Validar Stock
            if (producto.getStock() < item.getCantidad()) {
                return ResponseEntity.badRequest().body("Stock insuficiente para: " + producto.getNombre());
            }

            // Descontar Stock
            producto.setStock(producto.getStock() - item.getCantidad());
            productoRepository.save(producto);

            // Crear Detalle
            DetalleBoleta detalle = DetalleBoleta.builder()
                    .boleta(boleta)
                    .producto(producto)
                    .cantidad(item.getCantidad())
                    .precioUnitario(producto.getPrecio()) // Usamos precio de BD, no del frontend (seguridad)
                    .build();

            // Agregar a la lista y sumar al total
            boleta.getDetalles().add(detalle);
            totalCalculado += (producto.getPrecio() * item.getCantidad());
        }

        // D. Guardar Boleta Final con Total Real
        boleta.setTotal(totalCalculado);
        Boleta boletaGuardada = boletaRepository.save(boleta);

        // Retornamos la boleta completa (con ID generado)
        return ResponseEntity.ok(boletaGuardada);
    }

    // --- 2. OBTENER HISTORIAL DE UN USUARIO ---
    @GetMapping("/mis-compras")
    public ResponseEntity<List<Boleta>> getMisCompras(@RequestParam String email) {
        return ResponseEntity.ok(boletaRepository.findByUsuarioEmail(email));
    }

    // --- 3. OBTENER UNA BOLETA POR ID (Para la pantalla de éxito) ---
    @GetMapping("/{id}")
    public ResponseEntity<Boleta> getBoletaById(@PathVariable Long id) {
        return boletaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}