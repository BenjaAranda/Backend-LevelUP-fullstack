package com.duoc.backend_LevelUP.controllers;

import com.duoc.backend_LevelUP.models.Usuario;
import com.duoc.backend_LevelUP.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users") // NOTA: Ruta diferente a /auth
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    // 1. OBTENER TODOS (Para listar en el admin)
    @GetMapping
    public ResponseEntity<List<Usuario>> getAllUsers() {
        return ResponseEntity.ok(usuarioRepository.findAll());
    }

    // 2. OBTENER UNO POR ID (Para rellenar formulario de edición)
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getUserById(@PathVariable Long id) {
        Usuario user = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return ResponseEntity.ok(user);
    }

    // 3. MODIFICAR USUARIO
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> updateUser(@PathVariable Long id, @RequestBody Usuario usuarioDetalles) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Actualizamos datos básicos
        usuario.setNombre(usuarioDetalles.getNombre());
        usuario.setEmail(usuarioDetalles.getEmail());
        usuario.setEdad(usuarioDetalles.getEdad());
        usuario.setRole(usuarioDetalles.getRole());

        // Solo actualizamos la contraseña si el usuario escribió una nueva
        if (usuarioDetalles.getPassword() != null && !usuarioDetalles.getPassword().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(usuarioDetalles.getPassword()));
        }

        Usuario actualizado = usuarioRepository.save(usuario);
        return ResponseEntity.ok(actualizado);
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