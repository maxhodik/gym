package ua.hodik.gym.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.hodik.gym.dao.TraineeDao;
import ua.hodik.gym.model.Trainee;
import ua.hodik.gym.service.TraineeService;
import ua.hodik.gym.util.PasswordGenerator;
import ua.hodik.gym.util.UserNameGenerator;

import java.util.List;
import java.util.Objects;

@Service
public class TraineeServiceImpl implements TraineeService {

    private TraineeDao traineeDao;
    private UserNameGenerator userNameGenerator;
    private PasswordGenerator passwordGenerator;

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
        trainee.setUserId(userId);
        String firstName = trainee.getFirstName();
        String lastName = trainee.getLastName();
        String userName = userNameGenerator.generateUserName(firstName, lastName);
        String password = passwordGenerator.generatePassword();
        trainee.setUserName(userName);
        trainee.setPassword(password);
        return traineeDao.add(trainee);
    }


    @Override
    public Trainee update(Trainee trainee, int id) {
        Objects.requireNonNull(trainee, "Trainee can't be null");
        trainee.setUserId(id);
        return traineeDao.update(trainee, id);
    }

    @Override
    public boolean delete(int id) {
        return traineeDao.delete(id);
    }

    @Override
    public Trainee findById(int id) {
        return traineeDao.getById(id);
    }

    @Override
    public List<Trainee> getAllTrainees() {
        return traineeDao.getAllTrainees();
    }
}
