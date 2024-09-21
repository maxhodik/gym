package ua.hodik.gym.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;
import ua.hodik.gym.dto.TraineeDto;
import ua.hodik.gym.dto.UserCredentialDto;
import ua.hodik.gym.dto.mapper.TraineeMapper;
import ua.hodik.gym.dto.mapper.UserMapper;
import ua.hodik.gym.exception.ValidationException;
import ua.hodik.gym.exception.WrongCredentialException;
import ua.hodik.gym.model.Trainee;
import ua.hodik.gym.model.Trainer;
import ua.hodik.gym.model.User;
import ua.hodik.gym.repository.TraineeRepository;
import ua.hodik.gym.service.TraineeService;
import ua.hodik.gym.service.TrainerService;
import ua.hodik.gym.util.PasswordGenerator;
import ua.hodik.gym.util.UserNameGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
public class TraineeServiceImpl implements TraineeService {


    private UserNameGenerator userNameGenerator;
    private PasswordGenerator passwordGenerator;
    private final TraineeMapper traineeMapper;
    private final UserMapper userMapper;
    private final TraineeRepository traineeRepository;
    private final TrainerService trainerService;
    @Autowired
    private Validator validator;


    @Autowired
    public TraineeServiceImpl(TraineeMapper traineeMapper, UserMapper userMapper,
                              TraineeRepository traineeRepository, @Lazy TrainerService trainerService) {
        this.traineeMapper = traineeMapper;
        this.userMapper = userMapper;
        this.traineeRepository = traineeRepository;
        this.trainerService = trainerService;
    }


    @Autowired
    public void setUserNameGenerator(UserNameGenerator userNameGenerator) {
        this.userNameGenerator = userNameGenerator;
    }

    @Autowired
    public void setPasswordGenerator(PasswordGenerator passwordGenerator) {
        this.passwordGenerator = passwordGenerator;
    }


    @Override
    public void delete(int id) {
        traineeRepository.deleteById(id);
        log.info("Deleting Trainee with id= {}", id);

    }

    @Override
    public Trainee findById(int id) {
        Optional<Trainee> trainee = traineeRepository.findById(id);
        log.info("Finding trainee by id={}", id);
        return trainee.orElseThrow(() -> new EntityNotFoundException(String.format("Trainee id= %s not found", id)));

    }

    @Override
    public List<Trainee> getAllTrainees() {
        List<Trainee> allTrainees = traineeRepository.findAll();
        log.info("Finding all trainees from DB");
        return allTrainees;
    }


    @Override
    @Transactional
    public Trainee createTraineeProfile(TraineeDto traineeDto) {
        Trainee trainee = traineeMapper.convertToTrainee(traineeDto);
        setGeneratedUserName(trainee);
        setGeneratedPassword(trainee);
        trainee = traineeRepository.save(trainee);
        log.info("Trainee {} saved in DB", trainee.getUser().getUserName());
        return trainee;
    }

    private void setGeneratedPassword(Trainee trainee) {
        String password = passwordGenerator.generatePassword();
        trainee.getUser().setPassword(password);
    }

    private void setGeneratedUserName(Trainee trainee) {
        String firstName = trainee.getUser().getFirstName();
        String lastName = trainee.getUser().getLastName();
        String userName = userNameGenerator.generateUserName(firstName, lastName);
        trainee.getUser().setUserName(userName);

    }

    @Transactional(readOnly = true)
    public boolean matchCredential(@Validated UserCredentialDto credential) {
        validate(credential);

        String userName = credential.getUserName();
        Optional<Trainee> trainee = traineeRepository.findByUserUserName(userName);
        boolean result = trainee.isPresent() && trainee.get().getUser().getPassword().equals(credential.getPassword());
        log.info("User's {} credential matching is {}", userName, result);
        return result;
    }

    public void validate(UserCredentialDto credential) {
        Map<String, List<Map<String, String>>> validationResult = getMapOfErrors(credential);
        if (!validationResult.isEmpty()) {
            throw new ValidationException("Validation ran in service" + validationResult);
        }
    }

    private Map<String, List<Map<String, String>>> getMapOfErrors(UserCredentialDto credential) {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(credential, credential.getClass().getName());
        validator.validate(credential, bindingResult);

        return appendErrorsToMap(bindingResult);
    }

    private Map<String, List<Map<String, String>>> appendErrorsToMap(BeanPropertyBindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,  // Group by field name
                        Collectors.mapping(
                                error -> Map.of(
                                        "rejectedValue", error.getRejectedValue() != null ? error.getRejectedValue().toString() : "null",
                                        "message", error.getDefaultMessage() != null ? error.getDefaultMessage() : "No message"
                                ),
                                Collectors.toList()  // Collect errors for each field into a list
                        )
                ));
    }


    @Transactional()
    public Trainee changePassword(UserCredentialDto credential, String newPassword) {
        String userName = credential.getUserName();
        isMatchCredential(credential);
        Optional<Trainee> optionalTrainee = traineeRepository.findByUserUserName(userName);
        optionalTrainee.orElseThrow().getUser().setPassword(newPassword);
        log.info("{} password updated", userName);
        return optionalTrainee.orElseThrow();
    }

    @Transactional()
    public Trainee update(UserCredentialDto credential, TraineeDto traineeDto) {
        //todo valid username if it already exists
        String userName = credential.getUserName();
        isMatchCredential(credential);
        Optional<Trainee> optionalTrainee = traineeRepository.findByUserUserName(userName);
        Trainee trainee = traineeMapper.convertToTrainee(traineeDto);
        Trainee traineeToUpdate = optionalTrainee.orElseThrow(() -> new EntityNotFoundException("Trainee not found"));
        getTraineeToUpdate(traineeDto, trainee, traineeToUpdate);
        log.info("{} trainee updated", userName);
        return traineeToUpdate;
    }

    @Override
    @Transactional
    public void deleteTrainee(UserCredentialDto credential) {
        String userName = credential.getUserName();
        isMatchCredential(credential);
        traineeRepository.deleteByUserUserName(userName);
        log.info("{} trainee  deleted", userName);
    }

    private void getTraineeToUpdate(TraineeDto traineeDto, Trainee trainee, Trainee traineeToUpdate) {
        //todo
//        traineeToUpdate = traineeMapper.convertToTrainee(traineeDto);
        traineeToUpdate.setDayOfBirth(traineeDto.getDayOfBirth());
        traineeToUpdate.setAddress(traineeDto.getAddress());
        traineeToUpdate.getUser().setFirstName(trainee.getUser().getFirstName());
        traineeToUpdate.getUser().setLastName(trainee.getUser().getLastName());
        traineeToUpdate.getUser().setActive(trainee.getUser().isActive());
        traineeToUpdate.getUser().setUserName(trainee.getUser().getUserName());
        traineeToUpdate.getUser().setPassword(traineeToUpdate.getUser().getPassword());
    }

    @Transactional()
    public Trainee updateActiveStatus(UserCredentialDto credential, boolean isActive) {
        String userName = credential.getUserName();
        isMatchCredential(credential);
        Optional<Trainee> optionalTrainee = traineeRepository.findByUserUserName(userName);
        Trainee traineeToUpdate = optionalTrainee.orElseThrow(() -> new EntityNotFoundException("Trainee not found"));
        User user = traineeToUpdate.getUser();
        if (!user.isActive()) {
            user.setActive(isActive);
            log.info("Trainee {} active status updated", userName);
        }
        return traineeToUpdate;
    }

    @Transactional
    public void updateTrainersList(@Valid UserCredentialDto credential, String traineeUserName, List<String> trainers) {
        isMatchCredential(credential);
        Trainee trainee = traineeRepository.findByUserUserName(traineeUserName).orElseThrow(() ->
                new EntityNotFoundException(String.format("Trainee %S not found", traineeUserName)));
        List<Trainer> trainerList = new ArrayList<>();
        for (String trainerUserName : trainers) {
            Trainer trainer = trainerService.findByUserName(trainerUserName);
            trainerList.add(trainer);
        }
        trainee.addTrainersList(trainerList);
        log.info("Updating {} trainersList", traineeUserName);
    }


    private void isMatchCredential(UserCredentialDto credential) {
        if (!matchCredential(credential)) {
            throw new WrongCredentialException("Incorrect credentials, this operation is prohibited");
        }
    }
}
