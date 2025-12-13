package ua.edu.viti.military.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.edu.viti.military.entity.VehicleMovement;

import java.util.List;

@Repository
public interface VehicleMovementRepository extends JpaRepository<VehicleMovement, Long> {
    // Знайти історію машини, спочатку найновіші
    List<VehicleMovement> findByVehicleIdOrderByPerformedAtDesc(Long vehicleId);
}