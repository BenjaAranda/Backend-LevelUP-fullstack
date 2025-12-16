package com.duoc.backend_LevelUP.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(contact = @Contact(name = "Equipo Level-Up", email = "contacto@levelup-gamer.cl", url = "https://levelup-gamer.cl"), description = "API REST oficial para el E-Commerce Level-Up Gamer. "
        +
        "Provee endpoints para gestión de productos, autenticación de usuarios y procesamiento de ventas.", title = "Level-Up Gamer API", version = "1.0.0", license = @License(name = "Licencia Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0"), termsOfService = "https://levelup-gamer.cl/terminos"), servers = {
                @Server(description = "Servidor de Desarrollo Local", url = "http://localhost:8080"),
                @Server(description = "Servidor de Producción (AWS)", url = "https://tu-api-aws.com" // Opcional, solo
                                                                                                     // ilustrativo
                )
        }, security = {
                @SecurityRequirement(name = "bearerAuth")
        })
@SecurityScheme(name = "bearerAuth", description = "Autenticación JWT. Ingrese el token obtenido en /auth/login", scheme = "bearer", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", in = SecuritySchemeIn.HEADER)
public class OpenApiConfig {
}