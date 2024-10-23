package ua.hodik.gym.monitor;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class MemoryUsageHealthIndicator implements HealthIndicator {

    private static final long MEMORY_THRESHOLD = 100 * 1024 * 1024;

    @Override
    public Health health() {
        long freeMemory = Runtime.getRuntime().freeMemory();
        long totalMemory = Runtime.getRuntime().totalMemory();
        long usedMemory = totalMemory - freeMemory;

        if (usedMemory < MEMORY_THRESHOLD) {
            return Health.up()
                    .withDetail("usedMemory", usedMemory)
                    .withDetail("totalMemory", totalMemory)
                    .withDetail("status", "Memory usage is within limits")
                    .build();
        } else {
            return Health.down()
                    .withDetail("usedMemory", usedMemory)
                    .withDetail("totalMemory", totalMemory)
                    .withDetail("status", "Memory usage is too high")
                    .build();
        }
    }
}
