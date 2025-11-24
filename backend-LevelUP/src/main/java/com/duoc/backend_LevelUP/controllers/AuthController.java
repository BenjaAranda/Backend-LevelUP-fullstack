package com.duoc.backend_LevelUP.controllers;

import com.duoc.backend_LevelUP.dto.AuthResponse;
import com.duoc.backend_LevelUP.dto.LoginRequest;
import com.duoc.backend_LevelUP.dto.RegisterRequest;
import com.duoc.backend_LevelUP.models.Role;
import com.duoc.backend_LevelUP.models.Usuario;
import com.duoc.backend_LevelUP.repositories.UsuarioRepository;
import com.duoc.backend_LevelUP.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {

        // LÓGICA DE ROL:
        // Si en el JSON viene un rol ("ADMIN", "USER", etc.), lo usa.
        // Si viene vacío o nulo, asigna CLIENTE por defecto.
        Role roleAsignado = (request.getRole() != null) ? request.getRole() : Role.CLIENTE;

        Usuario user = Usuario.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(roleAsignado) // Asignamos el rol calculado arriba
                .edad(request.getEdad())
                .descuento(false)
                .build();

        usuarioRepository.save(user);

        String jwtToken = jwtService.generateToken(user);

        return ResponseEntity.ok(
                AuthResponse.builder()
                        .token(jwtToken)
                        .role(user.getRole().name())
                        .build());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()));

        Usuario user = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String jwtToken = jwtService.generateToken(user);

        return ResponseEntity.ok(
                AuthResponse.builder()
                        .token(jwtToken)
                        .role(user.getRole().name())
                        .build());
    }
}