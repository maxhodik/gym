package ua.hodik.gym.storage;


import org.springframework.beans.factory.annotation.Autowired;
import ua.hodik.gym.model.Trainee;

import java.util.Map;

public class StorageTraineeInitializer {
    //    @Value("${file.path.initialTraineeData}")
    private String path = "initialData.json";

    private Map<Integer, Trainee> traineeBD;

    @Autowired
    public void setTraineeBD(Map<Integer, Trainee> traineeBD) {
        this.traineeBD = traineeBD;
    }

//    @PostConstruct
//    public void initializeTraineeMap() {
//        List<Trainee> trainees;
//        try {
//            System.out.println(path);
//            File jsonFile = new ClassPathResource(path).getFile();
//            ObjectMapper objectMapper = new ObjectMapper();
//            trainees = objectMapper.readValue(jsonFile, new TypeReference<List<Trainee>>() {
//            });
//        } catch (IOException e) {
//            // todo handle Exception
//            throw new RuntimeException(e);
//        }
//        traineeBD = trainees.stream().collect(Collectors.toMap(Trainee::getUserId, Function.identity()));
//        System.out.println(traineeBD);
//    }
}
