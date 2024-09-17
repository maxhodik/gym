package ua.hodik.gym.model;

//@Entity
//@Table(name = "Training_Type")

public enum TrainingType {
    //    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private int id;
//
//    @Column(name = "name", nullable = false, unique = true)
//    private String name;
    BOXING(1), ZUMA(2), STRETCHING(3);

    int ID;

    TrainingType(int ID) {
        this.ID = ID;
    }

}
