package ua.edu.viti.military.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import ua.edu.viti.military.entity.Vehicle;

import java.time.LocalDateTime;

@Getter
public class MaintenanceStartedEvent extends ApplicationEvent {

    private final String vehicleNumber;
    private final String model;
    private final Integer mileage;
    private final String notes;
    private final LocalDateTime occurredAt;

    public MaintenanceStartedEvent(Object source, Vehicle vehicle, String notes) {
        super(source);
        this.vehicleNumber = vehicle.getRegistrationNumber();
        this.model = vehicle.getModel();
        this.mileage = vehicle.getMileage();
        this.notes = notes;
        this.occurredAt = LocalDateTime.now();
    }
}