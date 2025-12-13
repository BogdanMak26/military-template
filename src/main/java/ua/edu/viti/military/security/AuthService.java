package ua.edu.viti.military.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.viti.military.dto.request.LoginRequestDTO;
import ua.edu.viti.military.dto.request.RegisterRequestDTO;
import ua.edu.viti.military.dto.response.JwtResponseDTO;
import ua.edu.viti.military.entity.Role;
import ua.edu.viti.military.entity.RoleName;
import ua.edu.viti.military.entity.User;
import ua.edu.viti.military.exception.DuplicateResourceException; // Переконайтеся, що цей клас існує, або використовуйте RuntimeException
import ua.edu.viti.military.exception.ResourceNotFoundException;
import ua.edu.viti.military.repository.RoleRepository;
import ua.edu.viti.military.repository.UserRepository;
import ua.edu.viti.military.security.JwtUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public JwtResponseDTO login(LoginRequestDTO dto) {
        // 1. Спроба аутентифікації (Spring сам перевірить хеш пароля)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));

        // 2. Зберігаємо аутентифікацію в контексті
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Генеруємо токен
        String jwt = jwtUtils.generateToken(authentication);

        // 4. Дістаємо деталі користувача для відповіді
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return new JwtResponseDTO(jwt, user.getUsername(), user.getEmail(), roles);
    }

    @Transactional
    public void register(RegisterRequestDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new DuplicateResourceException("Username is already taken!");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("Email is already in use!");
        }

        // Створюємо нового користувача
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword())); // Обов'язково хешуємо пароль!
        user.setFullName(dto.getFullName());
        user.setMilitaryRank(dto.getMilitaryRank());
        user.setEnabled(true);

        // Призначаємо ролі
        Set<Role> roles = new HashSet<>();

        if (dto.getRoles() == null || dto.getRoles().isEmpty()) {
            // Якщо роль не вказана - даємо найменшу (VIEWER)
            Role userRole = roleRepository.findByName(RoleName.ROLE_VIEWER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            dto.getRoles().forEach(role -> {
                switch (role) {
                    case "admin":
                    case "ROLE_ADMIN":
                        Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    case "operator":
                    case "ROLE_OPERATOR":
                        Role modRole = roleRepository.findByName(RoleName.ROLE_OPERATOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(RoleName.ROLE_VIEWER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);
        log.info("Registered new user: {}", user.getUsername());
    }
}