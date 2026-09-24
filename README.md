# Level Up Gamer · API Spring Boot

Backend REST para la tienda Level Up Gamer, desarrollado con Spring Boot. Administra autenticación, usuarios, productos, categorías, ventas y boletas.

## Funcionalidades

- Registro e inicio de sesión.
- Seguridad basada en JWT.
- Gestión de usuarios y roles.
- CRUD de productos y categorías.
- Registro de ventas y generación de boletas.
- Persistencia con PostgreSQL.
- Documentación interactiva con OpenAPI/Swagger.
- Métricas y estado mediante Spring Boot Actuator.

## Tecnologías

- Java 21
- Spring Boot 3.4
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT
- Springdoc OpenAPI
- Maven

## Ejecución local

Requisitos: Java 21, Maven y PostgreSQL.

```bash
git clone https://github.com/BenjaAranda/Backend-LevelUP-fullstack.git
cd Backend-LevelUP-fullstack/backend-LevelUP
mvn spring-boot:run
```

La API usa el puerto `8080`. La interfaz de Swagger está disponible en `http://localhost:8080/swagger-ui/index.html`.

## Configuración segura

Configura la conexión a PostgreSQL y la clave JWT para tu propio entorno antes de ejecutar la aplicación. No publiques contraseñas ni secretos en `application.properties`; utiliza variables de entorno o un archivo local excluido de Git.

## Pruebas

```bash
mvn test
```

Proyecto desarrollado con fines académicos.
