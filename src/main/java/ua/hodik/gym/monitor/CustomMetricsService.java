package ua.hodik.gym.monitor;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class CustomMetricsService {

    private final Counter customMetricCounter;

    public CustomMetricsService(MeterRegistry meterRegistry) {
        customMetricCounter = Counter.builder("custom_metric_api_get_trainer")
                .description("Description of custom metric")
                .register(meterRegistry);
    }

    public void incrementCustomMetric() {
        customMetricCounter.increment();
    }
}