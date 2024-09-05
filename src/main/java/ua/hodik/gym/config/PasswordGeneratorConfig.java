package ua.hodik.gym.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Getter
@PropertySource("classpath:application.properties")
public class PasswordGeneratorConfig {
    @Value("${password.length}")
    private int passwordLength;
}

