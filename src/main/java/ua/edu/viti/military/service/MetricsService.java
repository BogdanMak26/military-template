package ua.edu.viti.military.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Service
public class MetricsService {

    private final Counter maintenanceStartedCounter;
    private final Counter maintenanceCompletedCounter;
    private final Counter driverAssignedCounter;
    private final Timer operationTimer;

    public MetricsService(MeterRegistry registry) {
        // Лічильник: скільки машин пішло на ремонт
        this.maintenanceStartedCounter = Counter.builder("military.vehicle.maintenance_started")
                .description("Кількість машин, відправлених на ремонт")
                .register(registry);

        // Лічильник: скільки повернулося
        this.maintenanceCompletedCounter = Counter.builder("military.vehicle.maintenance_completed")
                .description("Кількість машин, що повернулися з ремонту")
                .register(registry);

        // Лічильник: призначення водіїв
        this.driverAssignedCounter = Counter.builder("military.driver.assigned")
                .description("Кількість призначень водіїв")
                .register(registry);

        // Таймер: скільки часу займає операція
        this.operationTimer = Timer.builder("military.operation.duration")
                .description("Час виконання бізнес-операцій")
                .register(registry);
    }

    public void incrementMaintenanceStarted() {
        maintenanceStartedCounter.increment();
    }

    public void incrementMaintenanceCompleted() {
        maintenanceCompletedCounter.increment();
    }

    public void incrementDriverAssigned() {
        driverAssignedCounter.increment();
    }

    // Метод для заміру часу виконання шматка коду
    public <T> T measureTime(Supplier<T> operation) {
        long start = System.nanoTime();
        try {
            return operation.get();
        } finally {
            operationTimer.record(System.nanoTime() - start, TimeUnit.NANOSECONDS);
        }
    }
}