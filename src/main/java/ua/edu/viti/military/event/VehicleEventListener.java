package ua.edu.viti.military.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
public class VehicleEventListener {

    @Async // Виконується в окремому потоці
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT) // після коміту
    public void handleMaintenanceStarted(MaintenanceStartedEvent event) {
        log.info("⚡ ОТРИМАНО ПОДІЮ: Машина {} ({}) відправлена на ремонт.", event.getVehicleNumber(), event.getModel());


        try {
            log.info("📞 Дзвонимо начальнику рембату...");
            Thread.sleep(3000); // Спимо 3 секунди
            log.info("✅ Начальник рембату повідомлений. Запчастини замовлені.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Помилка при обробці події", e);
        }
    }
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMaintenanceCompleted(MaintenanceCompletedEvent event) {
        log.info("🎉 РЕМОНТ ЗАВЕРШЕНО: Машина {} знову в строю!", event.getVehicleNumber());
    }
}