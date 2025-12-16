package com.duoc.backend_LevelUP.controllers;

import com.duoc.backend_LevelUP.dto.UserResponse;
import com.duoc.backend_LevelUP.models.Usuario;
import com.duoc.backend_LevelUP.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    // 1. OBTENER TODOS (Versión Segura con DTO)
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<Usuario> usuarios = usuarioRepository.findAll();

        // Convertimos la lista de Entidades a lista de DTOs (Sin passwords)
        List<UserResponse> response = usuarios.stream()
                .map(u -> UserResponse.builder()
                        .id(u.getId())
                        .nombre(u.getNombre())
                        .email(u.getEmail())
                        .role(u.getRole())
                        .edad(u.getEdad())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // 2. OBTENER UNO POR ID
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Mapeamos a DTO
        UserResponse response = UserResponse.builder()
                .id(u.getId())
                .nombre(u.getNombre())
                .email(u.getEmail())
                .role(u.getRole())
                .edad(u.getEdad())
                .build();

        return ResponseEntity.ok(response);
    }

    // 3. MODIFICAR USUARIO (Especial para cambiar Roles)
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody Usuario usuarioDetalles) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Validamos qué campos vienen para no borrar datos accidentalmente
        if(usuarioDetalles.getNombre() != null) usuario.setNombre(usuarioDetalles.getNombre());
        if(usuarioDetalles.getEmail() != null) usuario.setEmail(usuarioDetalles.getEmail());
        if(usuarioDetalles.getEdad() != null) usuario.setEdad(usuarioDetalles.getEdad());

        // CAMBIO DE ROL (Lo más importante para el Admin)
        if(usuarioDetalles.getRole() != null) {
            usuario.setRole(usuarioDetalles.getRole());
        }

        // Solo actualizamos contraseña si viene una nueva y no está vacía
        if (usuarioDetalles.getPassword() != null && !usuarioDetalles.getPassword().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(usuarioDetalles.getPassword()));
        }

        Usuario actualizado = usuarioRepository.save(usuario);

        // Devolvemos el DTO actualizado
        return ResponseEntity.ok(UserResponse.builder()
                .id(actualizado.getId())
                .nombre(actualizado.getNombre())
                .email(actualizado.getEmail())
                .role(actualizado.getRole())
                .edad(actualizado.getEdad())
                .build());
    }

    // 4. ELIMINAR USUARIO
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        if (!usuarioRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        usuarioRepository.deleteById(id);
        return ResponseEntity.ok("Usuario eliminado correctamente");
    }
}