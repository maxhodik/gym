package ua.hodik.gym.monitor;

import jakarta.persistence.EntityManager;
import org.hibernate.query.Query;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JpaHealthIndicatorTest {

    @Mock
    private EntityManager entityManager;
    @Mock
    private Query mockQuery;

    @InjectMocks
    private JpaHealthIndicator jpaHealthIndicator;


    @Test
    void testHealth_Up() {
        //given
        when(entityManager.createQuery("SELECT 1")).thenReturn(mockQuery);
        when(mockQuery.getSingleResult()).thenReturn(1);
        //when
        Health health = jpaHealthIndicator.health();
        //then
        assertEquals(Status.UP, health.getStatus());
        assertEquals("Available", health.getDetails().get("gym_project"));
        assertEquals(1, health.getDetails().get("queryResult"));
    }

    @Test
    void testHealth_Down_QueryFailed() {
        //given
        when(entityManager.createQuery("SELECT 1")).thenReturn(mockQuery);
        when(mockQuery.getSingleResult()).thenReturn(null);
        //when
        Health health = jpaHealthIndicator.health();
        //then
        assertEquals(Status.DOWN, health.getStatus());
        assertEquals("Available but query failed", health.getDetails().get("database"));
    }

    @Test
    void testHealth_Down_Exception() {
        //given
        when(entityManager.createQuery("SELECT 1")).thenThrow(new RuntimeException("Database unavailable"));
        //when
        Health health = jpaHealthIndicator.health();
        //then
        assertEquals(Status.DOWN, health.getStatus());
        assertEquals("Unavailable", health.getDetails().get("database"));
        assertEquals("Database unavailable", health.getDetails().get("error"));
    }
}