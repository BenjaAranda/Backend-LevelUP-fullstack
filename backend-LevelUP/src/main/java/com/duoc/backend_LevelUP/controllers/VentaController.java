package com.duoc.backend_LevelUP.controllers;

import com.duoc.backend_LevelUP.models.*;
import com.duoc.backend_LevelUP.repositories.*;
import com.duoc.backend_LevelUP.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    @Data
    public static class CompraRequest {
        private List<ItemCompra> items;
    }

    @Data
    public static class ItemCompra {
        private String codigo;
        private Integer cantidad;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> realizarCompra(@RequestBody CompraRequest request, HttpServletRequest httpRequest) {

        // 1. Validar Token y obtener usuario
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("No autorizado");
        }
        String token = authHeader.substring(7);
        String email = jwtService.extractUsername(token);
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. Crear Boleta
        Boleta boleta = new Boleta();
        boleta.setUsuario(usuario);
        boleta.setFecha(LocalDateTime.now());
        boleta.setDetalles(new ArrayList<>());

        int totalCompra = 0;

        // 3. Procesar Productos
        for (ItemCompra item : request.getItems()) {
            Producto producto = productoRepository.findByCodigo(item.getCodigo())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + item.getCodigo()));

            if (producto.getStock() < item.getCantidad()) {
                return ResponseEntity.badRequest().body("Stock insuficiente para: " + producto.getNombre());
            }

            producto.setStock(producto.getStock() - item.getCantidad());
            productoRepository.save(producto);

            DetalleBoleta detalle = new DetalleBoleta();
            detalle.setProducto(producto);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(producto.getPrecio());
            detalle.setBoleta(boleta);

            boleta.getDetalles().add(detalle);
            totalCompra += (producto.getPrecio() * item.getCantidad());
        }

        boleta.setTotal(totalCompra);
        boletaRepository.save(boleta);

        return ResponseEntity.ok("Compra exitosa. Boleta ID: " + boleta.getId());
    }

    @GetMapping("/mis-compras")
    public List<Boleta> misCompras(HttpServletRequest httpRequest) {
        String authHeader = httpRequest.getHeader("Authorization");
        String email = jwtService.extractUsername(authHeader.substring(7));
        return boletaRepository.findByUsuarioEmail(email);
    }
}