package com.duoc.backend_LevelUP.controllers;

import com.duoc.backend_LevelUP.models.Categoria;
import com.duoc.backend_LevelUP.models.Producto;
import com.duoc.backend_LevelUP.repositories.CategoriaRepository;
import com.duoc.backend_LevelUP.repositories.ProductoRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ProductoController {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    // --- 1. Obtener todos los productos (Público) ---
    @GetMapping
    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    // --- 2. Obtener un producto por código (Público) ---
    @GetMapping("/{codigo}")
    public ResponseEntity<Producto> obtenerProducto(@PathVariable String codigo) {
        return productoRepository.findByCodigo(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // --- 3. Crear producto (Solo Admin debería poder, pero por ahora abierto si
    // Security lo permite) ---
    @PostMapping
    public ResponseEntity<Producto> crearProducto(@RequestBody ProductoDTO request) {

        // Validación básica
        if (productoRepository.findByCodigo(request.getCodigo()).isPresent()) {
            return ResponseEntity.badRequest().build(); // O lanzar excepción personalizada
        }

        // 1. Buscamos o creamos la categoría basada en el texto
        Categoria categoria = categoriaRepository.findByNombre(request.getCategoria())
                .orElseGet(() -> {
                    Categoria nueva = new Categoria();
                    nueva.setNombre(request.getCategoria());
                    return categoriaRepository.save(nueva);
                });

        // 2. Creamos el objeto Producto
        Producto producto = Producto.builder()
                .codigo(request.getCodigo())
                .nombre(request.getNombre())
                .imagen(request.getImg()) // Ojo: en tu modelo se llama 'imagen', en tu DTO 'img'
                .precio(request.getPrecio())
                .stock(request.getStock())
                .descripcion(request.getDescripcion())
                .categoria(categoria)
                .build();

        return ResponseEntity.ok(productoRepository.save(producto));
    }

    // --- DTO Auxiliar para recibir el JSON del Frontend ---
    @Data
    public static class ProductoDTO {
        private String codigo;
        private String img; // El frontend manda 'img', lo mapeamos a 'imagen' en la entidad
        private String nombre;
        private String categoria; // Texto plano (ej: "Juegos de Mesa")
        private Integer precio;
        private Integer stock;
        private String descripcion;
    }
}