package com.duoc.backend_LevelUP.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // Importante para diferenciar GET de POST
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // ==========================================
                        // 1. ACCESO PÚBLICO (Nadie necesita login)
                        // ==========================================
                        .requestMatchers("/api/v1/auth/**").permitAll() // Login y Registro
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Preflight CORS
                        .requestMatchers(
                                "/api/v1/swagger-ui/**",
                                "/api/v1/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // Permitimos VER productos a todo el mundo (invitados incluidos)
                        .requestMatchers(HttpMethod.GET, "/api/v1/productos/**").permitAll()

                        // ==========================================
                        // 2. SOLO ADMIN (Seguridad Crítica)
                        // ==========================================
                        // Solo el ADMIN puede tocar usuarios (borrar, ver lista, roles)
                        .requestMatchers("/api/v1/users/**").hasRole("ADMIN")

                        // ==========================================
                        // 3. ADMIN + VENDEDOR (Gestión de Tienda)
                        // ==========================================
                        // Aquí definimos lo que puede hacer el VENDEDOR (y el Admin por supuesto)
                        // Crear (POST), Actualizar (PUT), Borrar (DELETE) productos
                        .requestMatchers(HttpMethod.POST, "/api/v1/productos/**").hasAnyRole("ADMIN", "VENDEDOR")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/productos/**").hasAnyRole("ADMIN", "VENDEDOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/productos/**").hasAnyRole("ADMIN", "VENDEDOR")

                        // Si tienes endpoints de categorías, también van aquí:
                        .requestMatchers("/api/v1/categorias/**").hasAnyRole("ADMIN", "VENDEDOR")

                        // Gestión de órdenes (Asumiendo que el vendedor las gestiona)
                        .requestMatchers("/api/v1/ordenes/**").hasAnyRole("ADMIN")

                        // ==========================================
                        // 4. CLIENTES AUTENTICADOS (Resto)
                        // ==========================================
                        // Cualquier otra ruta no especificada arriba requiere al menos estar logueado
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Ajusta el puerto si tu frontend cambia (ej: 5173 es Vite default)
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}