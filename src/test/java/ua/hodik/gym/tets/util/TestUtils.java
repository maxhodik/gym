package ua.hodik.gym.tets.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;

public class TestUtils {
    public <T> T getUser(String filePath, Class<T> userType) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        File file = new File(filePath);
        try {
            return objectMapper.readValue(file, userType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
