package ua.edu.viti.military.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "vehicle_movements")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id") // Може бути null (наприклад, при списанні)
    private Driver driver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MovementType type;

    @Column(name = "mileage_at_event")
    private Integer mileageAtEvent; // Пробіг на момент операції (знімок)

    @Column(length = 500)
    private String notes; // Номер наказу, причина ремонту тощо

    @Column(name = "performed_by")
    private String performedBy; // Хто вніс запис (логін чергового)

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime performedAt;
}