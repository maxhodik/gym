package ua.hodik.gym.monitor;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class UnassignedTrainerCounterService {

    private final Counter customMetricCounter;

    public UnassignedTrainerCounterService(MeterRegistry meterRegistry) {
        customMetricCounter = Counter.builder("custom_metric_api_get_trainer")
                .description("a number of requests to /gym/unassigned-trainers/{traineeUsername} endpoint")
                .register(meterRegistry);
    }

    public void incrementCustomMetric() {
        customMetricCounter.increment();
    }
}