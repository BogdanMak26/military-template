package ua.edu.viti.military.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.edu.viti.military.entity.Role;
import ua.edu.viti.military.entity.RoleName;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}