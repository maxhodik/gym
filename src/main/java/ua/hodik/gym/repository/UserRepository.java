package ua.hodik.gym.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.hodik.gym.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUserName(String userNameDto);
}
