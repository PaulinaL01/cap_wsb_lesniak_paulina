package com.capgemini.wsb.fitnesstracker.training.internal;

import com.capgemini.wsb.fitnesstracker.training.api.Training;
import com.capgemini.wsb.fitnesstracker.training.api.TrainingProvider;
//import lombok.RequiredArgsConstructor;
import com.capgemini.wsb.fitnesstracker.user.api.User;
import com.capgemini.wsb.fitnesstracker.user.api.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/trainings")
public class TrainingController {
    @Autowired
    private final UserService userService;
    @Autowired
    private final TrainingRepository trainingRepository;
    @Autowired
    private final TrainingProvider trainingProvider;
    @Autowired
    private final TrainingServiceImpl trainingServiceImpl;

    public TrainingController(UserService userService, TrainingRepository trainingRepository, TrainingProvider trainingProvider, TrainingServiceImpl trainingServiceImpl) {
        this.userService = userService;
        this.trainingRepository = trainingRepository;
        this.trainingProvider = trainingProvider;
        this.trainingServiceImpl = trainingServiceImpl;
    }

    @PostMapping
    public ResponseEntity<Training> createTraining(@RequestBody TrainingRequest trainingRequest) {
        User user = userService.getUserById(trainingRequest.getUserId());
        Training training = buildTraining(trainingRequest, user);
        Training createdTraining = trainingProvider.createTraining(training);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTraining);
    }

    private Training buildTraining(TrainingRequest trainingRequest, User user) {
        return new Training(
                user,
                trainingRequest.getStartTime(),
                trainingRequest.getEndTime(),
                trainingRequest.getActivityType(),
                trainingRequest.getDistance(),
                trainingRequest.getAverageSpeed()
        );
    }

    @GetMapping("/{userId}")
    public List<Training> getTrainings(@PathVariable Long userId) {
        return trainingServiceImpl.getTrainingsByUserId(userId);
    }

    @GetMapping
    public ResponseEntity<List<Training>> getAllTrainings() {
        List<Training> trainings = trainingProvider.getAllTrainings();
        return ResponseEntity.ok(trainings);
    }

    @GetMapping("/activityType")
    public ResponseEntity<List<Training>> getTrainingsByActivityType(@RequestParam ActivityType activityType) {
        List<Training> trainings = trainingServiceImpl.getTrainingsByActivityType(activityType);

        return Optional.ofNullable(trainings)
                .filter(list -> !list.isEmpty())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/finished/{afterTime}")
    public ResponseEntity<List<Training>> getFinishedTrainings(@PathVariable String afterTime) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date parsedDate = dateFormat.parse(afterTime);
            List<Training> trainings = trainingServiceImpl.getFinishedTrainingsByAfterTime(parsedDate);

            return Optional.ofNullable(trainings)
                    .filter(list -> !list.isEmpty())
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (ParseException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{trainingId}")
    public ResponseEntity<Training> updateTrainingDetails(@PathVariable Long trainingId, @RequestBody Training updatedTrainingDetails) {
        return trainingRepository.findByTrainingId(trainingId)
                .map(existingTraining -> {
                    updatedTrainingDetails.setUser(existingTraining.getUser());
                    updatedTrainingDetails.setTrainingId(trainingId);
                    Training savedTraining = trainingRepository.save(updatedTrainingDetails);
                    return ResponseEntity.ok(savedTraining);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
