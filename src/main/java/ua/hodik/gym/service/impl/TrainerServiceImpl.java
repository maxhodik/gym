package ua.hodik.gym.service.impl;

import lombok.extern.log4j.Log4j2;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.hodik.gym.dto.TrainerDto;
import ua.hodik.gym.dto.TrainerUpdateDto;
import ua.hodik.gym.dto.UserCredentialDto;
import ua.hodik.gym.exception.MyEntityNotFoundException;
import ua.hodik.gym.model.Trainer;
import ua.hodik.gym.model.TrainingType;
import ua.hodik.gym.model.User;
import ua.hodik.gym.repository.TrainerRepository;
import ua.hodik.gym.repository.UserRepository;
import ua.hodik.gym.service.TrainerService;
import ua.hodik.gym.service.UserService;
import ua.hodik.gym.service.mapper.TrainerMapper;
import ua.hodik.gym.util.CredentialChecker;
import ua.hodik.gym.util.PasswordGenerator;
import ua.hodik.gym.util.UserNameGenerator;

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class TrainerServiceImpl implements TrainerService {
    public static final String TRANSACTION_ID = "TransactionId";
    private final UserNameGenerator userNameGenerator;
    private final PasswordGenerator passwordGenerator;
    private final TrainerRepository trainerRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final TrainerMapper trainerMapper;
    private final CredentialChecker credentialChecker;


    @Autowired
    public TrainerServiceImpl(UserNameGenerator userNameGenerator, PasswordGenerator passwordGenerator,
                              TrainerRepository trainerRepository, UserRepository userRepository, UserService userService, TrainerMapper trainerMapper,
                              CredentialChecker credentialChecker) {
        this.userNameGenerator = userNameGenerator;
        this.passwordGenerator = passwordGenerator;
        this.trainerRepository = trainerRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.trainerMapper = trainerMapper;
        this.credentialChecker = credentialChecker;
    }

    @Override
    @Transactional
    public UserCredentialDto createTrainerProfile(TrainerDto trainerDto) {
        Trainer trainer = trainerMapper.convertToTrainer(trainerDto);
        setGeneratedUserName(trainer);
        setGeneratedPassword(trainer);
        trainer.getUser().setActive(true);
        trainer = trainerRepository.save(trainer);
        User user = trainer.getUser();
        UserCredentialDto credentialDto = new UserCredentialDto(user.getUserName(), user.getPassword());
        log.debug("[TrainerService] Registration trainer  username {}, TransactionId {}", credentialDto.getUserName(), MDC.get(TRANSACTION_ID));
        return credentialDto;
    }

    @Transactional
    @Override
    public TrainerDto update(int trainerId, TrainerUpdateDto trainerDto) {
        Trainer trainerToUpdate = findById(trainerId);
        User updatedUser = userService.update(trainerToUpdate.getUser().getId(), trainerDto.getUserUpdateDto());
        trainerToUpdate.setUser(updatedUser);
        updateTrainer(trainerDto, trainerToUpdate);
        log.debug("[TrainerController] Trainer id={} updated, TransactionId {}", trainerId, MDC.get(TRANSACTION_ID));
        return trainerMapper.convertToTrainerDto(trainerToUpdate);
    }

    @Override
    @Transactional(readOnly = true)
    public Trainer findById(int id) {
        Optional<Trainer> trainer = trainerRepository.findById(id);
        log.info("Finding trainer by id={}", id);
        return trainer.orElseThrow(() -> new MyEntityNotFoundException(String.format("Trainer id= %s not found", id)));
    }

    @Override
    public Trainer findByUserName(String trainerUserName) {
        log.info("Finding trainer by userName {}", trainerUserName);
        return trainerRepository.findByUserUserName(trainerUserName).orElseThrow(() ->
                new MyEntityNotFoundException(String.format("Trainer %s not found", trainerUserName)));
    }


    @Override
    public TrainerDto findTrainerDtoByUserName(String trainerUserName) {
        log.debug("[TrainerService] Finding trainer  username {}, TransactionId {}", trainerUserName, MDC.get(TRANSACTION_ID));
        return trainerMapper.convertToTrainerDto(findByUserName(trainerUserName));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trainer> getAllTrainers() {
        List<Trainer> allTrainers = trainerRepository.findAll();
        log.info("Finding all trainers from DB");
        return allTrainers;
    }



    @Transactional
    @Override
    public void updateActiveStatus(String userName, boolean isActive) {
        Trainer trainerToUpdate = findByUserName(userName);
        User user = trainerToUpdate.getUser();
        if (!user.isActive() == isActive) {
            user.setActive(isActive);
            log.debug("[TrainerController] Trainer {} active status updated, TransactionId {}", userName, MDC.get(TRANSACTION_ID));
        }
    }

    @Override
    public List<TrainerDto> getNotAssignedTrainers(String traineeName) {
        List<TrainerDto> trainerDtos = trainerRepository.findAllNotAssignedTrainers(traineeName).stream()
                .map(trainerMapper::convertToTrainerDto)
                .toList();
        log.debug("[TrainerService] List not assigned trainers found. Trainee username {}, TransactionId {}", traineeName, MDC.get(TRANSACTION_ID));
        return trainerDtos;
    }


    private void setGeneratedPassword(Trainer trainer) {
        String password = passwordGenerator.generatePassword();
        trainer.getUser().setPassword(password);
    }

    private void setGeneratedUserName(Trainer trainer) {
        String firstName = trainer.getUser().getFirstName();
        String lastName = trainer.getUser().getLastName();
        String userName = userNameGenerator.generateUserName(firstName, lastName);
        trainer.getUser().setUserName(userName);
    }

    private void updateTrainer(TrainerUpdateDto trainerDto, Trainer trainerToUpdate) {
        trainerToUpdate.setSpecialization(TrainingType.valueOf(trainerDto.getSpecialization()));
    }
}
