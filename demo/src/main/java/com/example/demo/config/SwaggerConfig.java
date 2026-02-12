package com.example.demo.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger/OpenAPI Configuration
 * Auto-generates API documentation accessible at /swagger-ui.html
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Multi-Tenant Inventory & Order Management System API")
                        .version("1.0.0")
                        .description("""
                                **Production-Grade SaaS Backend with Spring Boot**
                                
                                ## Features:
                                - 🔐 JWT-based Authentication & Authorization
                                - 🏢 Multi-Tenant Architecture with Data Isolation
                                - 📦 Inventory Management with Stock Tracking
                                - 🛒 Order Processing with Transactional Stock Deduction
                                - 🔍 Search, Filtering, and Pagination
                                - ⚡ Redis Caching for Performance
                                - 📊 Low Stock Alerts
                                - 🛡️ Role-Based Access Control (RBAC)
                                
                                ## Authentication:
                                1. Register a user via `/auth/register`
                                2. Login via `/auth/login` to get JWT token
                                3. Use the token in 'Authorize' button above
                                4. Format: `Bearer <your-token>`
                                
                                ## Roles:
                                - **SUPER_ADMIN**: Platform administrator
                                - **ADMIN**: Organization administrator
                                - **STAFF**: Regular employee
                                """)
                        .contact(new Contact()
                                .name("API Support")
                                .email("support@ims.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080/api")
                                .description("Local Development Server"),
                        new Server()
                                .url("https://api.ims.com/api")
                                .description("Production Server")))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Enter JWT token obtained from /auth/login")));
    }
}
