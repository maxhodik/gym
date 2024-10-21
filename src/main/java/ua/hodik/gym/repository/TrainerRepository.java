package ua.hodik.gym.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.hodik.gym.model.Trainer;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Integer> {
    Optional<Trainer> findByUserUserName(String userName);

    @Query("SELECT t FROM Trainer t " +
            "WHERE t.id NOT IN (SELECT tr.id FROM Trainer tr " +
            "JOIN tr.trainees trainee WHERE trainee.user.userName = :username)" +
            "AND t.user.isActive = true")
    List<Trainer> findAllNotAssignedTrainers(@Param("username") String username);
}
