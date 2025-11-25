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
@OpenAPIDefinition(info = @Info(contact = @Contact(name = "Equipo Level-Up", email = "contacto@levelup.cl", url = "https://levelup-gamer.cl"), description = "Documentación de la API REST para el E-Commerce Level-Up Gamer.", title = "Level-Up Gamer API", version = "1.0", license = @License(name = "Licencia Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0"), termsOfService = "Términos de servicio"), servers = {
                @Server(description = "Servidor Local", url = "http://localhost:8080")
}, security = {
                @SecurityRequirement(name = "bearerAuth")
})
@SecurityScheme(name = "bearerAuth", description = "Autenticación JWT. Ingrese el token en el formato: Bearer <token>", scheme = "bearer", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", in = SecuritySchemeIn.HEADER)
public class OpenApiConfig {
}
