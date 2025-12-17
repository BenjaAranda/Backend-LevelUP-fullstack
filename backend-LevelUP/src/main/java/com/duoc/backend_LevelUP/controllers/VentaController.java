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
    @Transactional
    public ResponseEntity<?> crearVenta(@RequestBody VentaRequest request) {

        Usuario usuario = usuarioRepository.findByEmail(request.getUsuarioEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + request.getUsuarioEmail()));

        // Construir la dirección completa en un string (o usar campos separados si
        // prefieres)
        String direccionCompleta = request.getCalle() + " " + request.getNumero();
        if (request.getDepartamento() != null && !request.getDepartamento().isEmpty()) {
            direccionCompleta += ", Depto " + request.getDepartamento();
        }

        Boleta boleta = Boleta.builder()
                .fecha(LocalDateTime.now())
                .total(0)
                .usuario(usuario)
                // --- NUEVO: Guardar datos de envío y estado ---
                .direccion(direccionCompleta)
                .comuna(request.getComuna())
                .region(request.getRegion())
                .telefonoContacto(request.getTelefono())
                .estado(Boleta.EstadoPedido.PENDIENTE) // Estado inicial por defecto
                .detalles(new ArrayList<>())
                .build();

        int totalCalculado = 0;

        for (var item : request.getItems()) {
            Producto producto = productoRepository.findByCodigo(item.getProductoCodigo())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + item.getProductoCodigo()));

            if (producto.getStock() < item.getCantidad()) {
                return ResponseEntity.badRequest().body("Stock insuficiente para: " + producto.getNombre());
            }

            producto.setStock(producto.getStock() - item.getCantidad());
            productoRepository.save(producto);

            DetalleBoleta detalle = DetalleBoleta.builder()
                    .boleta(boleta)
                    .producto(producto)
                    .cantidad(item.getCantidad())
                    .precioUnitario(producto.getPrecio())
                    .build();

            boleta.getDetalles().add(detalle);
            totalCalculado += (producto.getPrecio() * item.getCantidad());
        }

        boleta.setTotal(totalCalculado);
        Boleta boletaGuardada = boletaRepository.save(boleta);

        return ResponseEntity.ok(boletaGuardada);
    }

    @GetMapping("/mis-compras")
    public ResponseEntity<List<Boleta>> getMisCompras(@RequestParam String email) {
        return ResponseEntity.ok(boletaRepository.findByUsuarioEmail(email));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Boleta> getBoletaById(@PathVariable Long id) {
        return boletaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}