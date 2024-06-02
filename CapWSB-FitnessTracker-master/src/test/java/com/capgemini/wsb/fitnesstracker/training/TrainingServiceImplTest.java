package com.capgemini.wsb.fitnesstracker.training;

import com.capgemini.wsb.fitnesstracker.training.api.Training;
import com.capgemini.wsb.fitnesstracker.training.api.TrainingProvider;
import com.capgemini.wsb.fitnesstracker.training.internal.ActivityType;
import com.capgemini.wsb.fitnesstracker.training.internal.TrainingRepository;
import com.capgemini.wsb.fitnesstracker.training.internal.TrainingServiceImpl;
import com.capgemini.wsb.fitnesstracker.user.api.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@Transactional
class TrainingServiceImplTest {

    private TrainingProvider trainingProvider;

    @Mock
    private TrainingRepository trainingRepository;

    private Training trainingAsgard;
    private Training trainingHome;
    private Training trainingManatee;
    private Training trainingGreek;
    private Training trainingEnd;

@BeforeEach
public void setUp() {
    MockitoAnnotations.initMocks(this);
    trainingProvider = new TrainingServiceImpl(trainingRepository);

    User user1 = new User("Romek", "Duda", LocalDate.of(1876, 8, 5), "romekduda@gmail.com");
    User user2 = new User("Monika", "Nudziara", LocalDate.of(1766, 5, 6), "monikanudziara@gmail.com");
    User user3 = new User("Barbara", "Kunekunda", LocalDate.of(2023, 6, 6), "barbarakunekunda@gmail.com");
    User user4 = new User("Alan", "Lastone", LocalDate.of(2000,3,3),"alanlastone@gmail.com");
    Date startTime = new Date();

    Date endTime1 = new Date(startTime.getTime() + 13000);
    Date endTime2 = new Date(startTime.getTime() + 15000);
    Date endTime3 = new Date(startTime.getTime() + 45000);
    Date endTime4 = new Date(startTime.getTime() + 20000);

    trainingAsgard = new Training(user2, startTime, endTime1, ActivityType.SWIMMING, 666.0, 0.6);
    trainingHome = new Training(user1, startTime, endTime2, ActivityType.RUNNING, 606.0, 6);
    trainingManatee = new Training(user3, startTime, endTime3, ActivityType.CYCLING, 55.0, 45.0);
    trainingGreek = new Training(user1, startTime, endTime4, ActivityType.WALKING, 10.0, 5.5);
    trainingEnd = new Training(user4,startTime,endTime1,ActivityType.CYCLING,34,35);
}


    @Test
    void shouldCreateTraining() {

        when(trainingRepository.save(trainingAsgard)).thenReturn(trainingAsgard);

        Training training = trainingProvider.createTraining(trainingAsgard);

        assertEquals(trainingAsgard, training);
    }

    @Test
    void shouldGetExistingTrainings() {

        List<Training> trainings = Arrays.asList(trainingAsgard, trainingHome, trainingManatee,trainingEnd);
        when(trainingRepository.findAll()).thenReturn(trainings);

        List<Training> allTrainings = trainingProvider.getAllTrainings();

        assertEquals(trainings, allTrainings);
    }

    @Test
    void shouldGetExistingTrainingsByUserId() {
        Long userId = 1L;
        when(trainingRepository.findByUserId(userId)).thenReturn(Arrays.asList(trainingAsgard, trainingHome, trainingGreek,trainingManatee));

        Iterable<Training> userTrainings = trainingProvider.getTrainingsByUserId(userId);
        List<Training> userTrainingsIterable = (List<Training>) userTrainings;

        assertEquals(4, userTrainingsIterable.size());
        assertTrue(userTrainingsIterable.contains(trainingAsgard));
        assertTrue(userTrainingsIterable.contains(trainingHome));
        assertTrue(userTrainingsIterable.contains(trainingGreek));
        assertTrue(userTrainingsIterable.contains(trainingManatee));
    }

    @Test
    void shouldGetExistingTrainingsByActivityType() {

        ActivityType activityType = ActivityType.RUNNING;
        when(trainingRepository.findByActivityType(activityType)).thenReturn(Arrays.asList(trainingAsgard, trainingGreek,trainingEnd));

        List<Training> trainings = trainingProvider.getTrainingsByActivityType(activityType);

        assertEquals(3, trainings.size());
        assertTrue(trainings.contains(trainingAsgard));
        assertTrue(trainings.contains(trainingGreek));
        assertTrue(trainings.contains(trainingEnd));
    }

    @Test
    void shouldGetExistingFinishedTrainingsAfterGivenTime() {
        Date date = new Date();
        when(trainingRepository.findByEndTimeAfter(date)).thenReturn(Arrays.asList(trainingAsgard, trainingHome, trainingGreek,trainingManatee));

        Iterable<Training> trainings = trainingProvider.getFinishedTrainingsByAfterTime(date);
        List<Training> completedTrainings = (List<Training>) trainings;

        assertEquals(4, completedTrainings.size());
        assertTrue(completedTrainings.contains(trainingAsgard));
        assertTrue(completedTrainings.contains(trainingHome));
        assertTrue(completedTrainings.contains(trainingGreek));
        assertTrue(completedTrainings.contains(trainingManatee));
    }
}
