package ua.edu.viti.military.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import ua.edu.viti.military.entity.Vehicle;
import java.time.LocalDateTime;

@Getter
public class MaintenanceCompletedEvent extends ApplicationEvent {
    private final String vehicleNumber;
    private final LocalDateTime completedAt;

    public MaintenanceCompletedEvent(Object source, Vehicle vehicle) {
        super(source);
        this.vehicleNumber = vehicle.getRegistrationNumber();
        this.completedAt = LocalDateTime.now();
    }
}