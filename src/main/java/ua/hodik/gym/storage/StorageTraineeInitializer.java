package ua.hodik.gym.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import ua.hodik.gym.model.Trainee;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class StorageTraineeInitializer {
    //    @Value("${file.path.initialTraineeData}")
    private String path = "initialTraineeData.json";

    private Map<Integer, Trainee> traineeBD;

    @Autowired
    public void setTraineeBD(Map<Integer, Trainee> traineeBD) {
        this.traineeBD = traineeBD;
    }

    @PostConstruct
    public void initializeTraineeMap() {
        List<Trainee> trainees;
        try {
            System.out.println(path);
            File jsonFile = new ClassPathResource(path).getFile();
            ObjectMapper objectMapper = new ObjectMapper();
            trainees = objectMapper.readValue(jsonFile, new TypeReference<List<Trainee>>() {
            });
        } catch (IOException e) {
            // todo handle Exception
            throw new RuntimeException(e);
        }
        traineeBD = trainees.stream().collect(Collectors.toMap(Trainee::getUserId, Function.identity()));
        System.out.println(traineeBD);
    }
}
