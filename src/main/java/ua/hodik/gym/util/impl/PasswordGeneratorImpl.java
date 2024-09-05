package ua.hodik.gym.util.impl;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.hodik.gym.config.PasswordGeneratorConfig;
import ua.hodik.gym.util.PasswordGenerator;

@Component
public class PasswordGeneratorImpl implements PasswordGenerator {


    private PasswordGeneratorConfig passwordGeneratorConfig;

    @Autowired
    public void setPasswordGeneratorConfig(PasswordGeneratorConfig passwordGeneratorConfig) {
        this.passwordGeneratorConfig = passwordGeneratorConfig;
    }


    @Override
    public String generatePassword() {
        return RandomStringUtils.randomAlphabetic(passwordGeneratorConfig.getPasswordLength());
    }
}
