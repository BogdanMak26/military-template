package ua.edu.viti.military.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicles")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String model;

    @Column(name = "registration_number", nullable = false, unique = true, length = 20)
    private String registrationNumber;


    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private VehicleCategory category;

    @Column(name = "engine_number", unique = true, length = 50)
    private String engineNumber;

    @Column(name = "chassis_number", unique = true, length = 50)
    private String chassisNumber;

    @Column(name = "manufacture_year")
    private Integer manufactureYear;

    @Column(nullable = false)
    private Integer mileage; // пробіг в км

    @Enumerated(EnumType.STRING)
    @Column(name = "fuel_type", nullable = false)
    private FuelType fuelType;

    @Column(name = "fuel_consumption")
    private Double fuelConsumption; // літрів на 100 км

    @Column(name = "maintenance_interval_km")
    private Integer maintenanceIntervalKm;

    @Column(name = "last_maintenance_date")
    private LocalDate lastMaintenanceDate;

    @Column(name = "last_maintenance_mileage")
    private Integer lastMaintenanceMileage;

    // Зв'язок ManyToOne з водієм
    @ManyToOne
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleStatus status;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Version
    private Long version;
}