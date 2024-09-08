package ua.hodik.gym.util.impl;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.hodik.gym.config.PasswordGeneratorConfig;
import ua.hodik.gym.util.PasswordGenerator;

@Component
@Log4j2
public class PasswordGeneratorImpl implements PasswordGenerator {


    private PasswordGeneratorConfig passwordGeneratorConfig;

    @Autowired
    public void setPasswordGeneratorConfig(PasswordGeneratorConfig passwordGeneratorConfig) {
        this.passwordGeneratorConfig = passwordGeneratorConfig;
    }


    @Override
    public String generatePassword() {
        String password = RandomStringUtils.randomAlphabetic(passwordGeneratorConfig.getPasswordLength());
        log.info("Password generated successfully");
        return password;
    }
}
