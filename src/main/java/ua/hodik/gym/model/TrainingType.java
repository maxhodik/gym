package ua.hodik.gym.model;

import lombok.Getter;

@Getter
public enum TrainingType {

    BOXING(1), ZUMBA(2), STRETCHING(3);

    int ID;

    TrainingType(int ID) {
        this.ID = ID;
    }

}
