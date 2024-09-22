package ua.hodik.gym.tets.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class TestUtils {
    public static <T> T readFromFile(String filePath, Class<T> type) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        File file = new File(filePath);
        try (InputStream stream = TestUtils.class.getClassLoader().getResourceAsStream(filePath)) {
            return objectMapper.readValue(stream, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
