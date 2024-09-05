package ua.hodik.gym.util.UtilImpl;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ua.hodik.gym.util.PasswordGenerator;

@Component("passwordGenerator")
public class PasswordGeneratorImpl implements PasswordGenerator {

    @Value("${password.length}")
    int passwordLength;

    @Override
    public String generatePassword() {
        return RandomStringUtils.randomAlphabetic(passwordLength);
    }
}
