package ua.edu.viti.military.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class RegisterRequestDTO {
    @NotBlank
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, message = "Пароль має бути мінімум 6 символів")
    private String password;

    @NotBlank
    private String fullName;

    private String militaryRank;

    private Set<String> roles; // Наприклад: ["ROLE_OPERATOR", "ROLE_ADMIN"]
}