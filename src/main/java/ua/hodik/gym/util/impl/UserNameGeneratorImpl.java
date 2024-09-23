package ua.hodik.gym.util.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ua.hodik.gym.model.User;
import ua.hodik.gym.service.UserService;
import ua.hodik.gym.util.UserNameGenerator;

@Component
@Log4j2
public class UserNameGeneratorImpl implements UserNameGenerator {
    @Autowired
    @Lazy
    private UserService userService;


    @Override
    public String generateUserName(String firstName, String lastName) {
        String userName;
        String baseUsername = generateBaseUserName(firstName, lastName);

        long count = userService.getAllUsers().stream()
                .map(User::getUserName)
                .map(u -> u.replaceAll("\\d+", ""))
                .filter(u -> u.equals(baseUsername))
                .count();
        if (count >= 1) {
            userName = baseUsername + count;
            log.info("UserName {} created", userName);
            return userName;
        }
        log.info("UserName {} created", baseUsername);
        return baseUsername;
    }

    private String generateBaseUserName(String firstName, String lastName) {
        return firstName + "." + lastName;
    }

}
