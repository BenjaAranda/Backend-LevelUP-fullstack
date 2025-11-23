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

    // --- Obtener todos los productos ---
    @GetMapping
    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    // --- Obtener un producto por código ---
    @GetMapping("/{codigo}")
    public ResponseEntity<Producto> obtenerProducto(@PathVariable String codigo) {
        return productoRepository.findByCodigo(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // --- Crear producto (Compatible con tu React) ---
    @PostMapping
    public ResponseEntity<Producto> crearProducto(@RequestBody ProductoDTO request) {
        // 1. Buscamos o creamos la categoría basada en el texto que envía el frontend
        Categoria categoria = categoriaRepository.findByNombre(request.getCategoria())
                .orElseGet(() -> {
                    Categoria nueva = new Categoria();
                    nueva.setNombre(request.getCategoria());
                    return categoriaRepository.save(nueva);
                });

        // 2. Creamos el producto real
        Producto producto = new Producto();
        producto.setCodigo(request.getCodigo());
        producto.setNombre(request.getNombre());
        producto.setImg(request.getImg());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        producto.setDescripcion(request.getDescripcion());
        producto.setCategoria(categoria); // Asignamos el objeto categoría

        return ResponseEntity.ok(productoRepository.save(producto));
    }

    // DTO: Clase auxiliar para recibir los datos tal cual los manda tu Frontend
    // (con categoría string)
    @Data
    public static class ProductoDTO {
        private String codigo;
        private String img;
        private String nombre;
        private String categoria; // Aquí recibimos el texto "Juegos de Mesa"
        private Integer precio;
        private Integer stock;
        private String descripcion;
    }
}