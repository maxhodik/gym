package ua.hodik.gym.monitor;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class JpaHealthIndicator implements HealthIndicator {

    @Autowired
    private EntityManager entityManager;

    @Override
    public Health health() {
        try {
            Object result = entityManager.createQuery("SELECT 1").getSingleResult();

            if (result != null) {
                return Health.up()
                        .withDetail("gym_project", "Available")
                        .withDetail("queryResult", result)
                        .build();
            } else {
                return Health.down()
                        .withDetail("database", "Available but query failed")
                        .withDetail("queryResult", result)
                        .build();
            }
        } catch (Exception e) {
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .withDetail("database", "Unavailable")
                    .build();
        }
    }
}
