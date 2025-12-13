package ua.edu.viti.military.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // 1. Інформація про API (Ваш код)
                .info(new Info()
                        .title("Military Transport API - Варіант B")
                        .version("1.0.0")
                        .description(
                                "REST API для системи управління військовим автопарком (ВІТІ).\n\n" +
                                        "**Основний функціонал:**\n" +
                                        "- 🚛 **Транспорт:** Облік, контроль пробігу, статусів та пального\n" +
                                        "- 🔧 **ТО:** Відстеження необхідності технічного обслуговування\n" +
                                        "- 👮 **Водії:** База даних водіїв, контроль прав та закріплення за технікою\n" +
                                        "- 📂 **Категорії:** Довідник типів техніки\n\n" +
                                        "**Технології:** Spring Boot 3, PostgreSQL, Docker"
                        )
                        .contact(new Contact()
                                .name("Курсант Макаренко")
                                .email("student@viti.edu.ua")
                        )
                )
                .servers(List.of(
                        new Server().url("http://localhost:8081").description("Local Development Server")
                ))
                // 👇 2. ДОДАЄМО КНОПКУ AUTHORIZE 👇
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", createSecurityScheme()));
    }

    // Допоміжний метод для налаштування схеми
    private SecurityScheme createSecurityScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }
}