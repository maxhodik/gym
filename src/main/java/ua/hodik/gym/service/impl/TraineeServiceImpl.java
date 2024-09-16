package ua.hodik.gym.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.hodik.gym.dao.TraineeDao;
import ua.hodik.gym.dto.TraineeDto;
import ua.hodik.gym.dto.mapper.TraineeMapper;
import ua.hodik.gym.model.Trainee;
import ua.hodik.gym.repository.TraineeRepository;
import ua.hodik.gym.service.TraineeService;
import ua.hodik.gym.util.PasswordGenerator;
import ua.hodik.gym.util.UserNameGenerator;

import java.util.List;
import java.util.Objects;

@Service
@Log4j2
public class TraineeServiceImpl implements TraineeService {

    private TraineeDao traineeDao;
    private UserNameGenerator userNameGenerator;
    private PasswordGenerator passwordGenerator;
    private final TraineeMapper traineeMapper;
    private final TraineeRepository traineeRepository;


    @Autowired
    public TraineeServiceImpl(TraineeMapper traineeMapper, TraineeRepository traineeRepository) {
        this.traineeMapper = traineeMapper;
        this.traineeRepository = traineeRepository;
    }

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
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
    public Trainee create(Trainee trainee) {
        Objects.requireNonNull(trainee, "Trainee can't be null");
        int userId = traineeDao.getMaxId() + 1;
        trainee.setTraineeId(userId);
        String firstName = trainee.getUser().getFirstName();
        String lastName = trainee.getUser().getLastName();
        String userName = userNameGenerator.generateUserName(firstName, lastName);
        String password = passwordGenerator.generatePassword();
        trainee.getUser().setUserName(userName);
        trainee.getUser().setPassword(password);
        Trainee addedTrainee = traineeDao.add(trainee);
        log.info("Trainee {} added successfully", userName);
        return addedTrainee;
    }


    @Override
    public Trainee update(Trainee trainee, int id) {
        Objects.requireNonNull(trainee, "Trainee can't be null");
        trainee.setTraineeId(id);
        Trainee updatedTrainee = traineeDao.update(trainee, id);
        log.info("Trainee with id ={} updated", id);
        return updatedTrainee;
    }

    @Override
    public boolean delete(int id) {
        boolean delete = traineeDao.delete(id);
        log.info("Deleting Trainee with id= {}", id);
        return delete;
    }

    @Override
    public Trainee findById(int id) {
        Trainee traineeById = traineeDao.getById(id);
        log.info("Finding trainee by id={}", id);
        return traineeById;
    }

    @Override
    public List<Trainee> getAllTrainees() {
        List<Trainee> allTrainees = traineeDao.getAllTrainees();
        log.info("Finding all trainees");
        return allTrainees;
    }


    @Override
    public Trainee createTraineeProfile(TraineeDto traineeDto) {
        Trainee trainee = traineeMapper.convertToTrainee(traineeDto);
        setGeneratedUserName(trainee);
        setGeneratedPassword(trainee);
        trainee = traineeRepository.saveAndFlush(trainee);
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
}
