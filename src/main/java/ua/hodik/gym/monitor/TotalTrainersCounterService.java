package ua.hodik.gym.monitor;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.hodik.gym.repository.TrainerRepository;


@Component
public class TotalTrainersCounterService {

    @Autowired
    public TotalTrainersCounterService(TrainerRepository trainerRepository, MeterRegistry meterRegistry) {
        Gauge.builder("trainer_count", trainerRepository::count)
                .description("A current number of trainers in the system")
                .register(meterRegistry);
    }
}
