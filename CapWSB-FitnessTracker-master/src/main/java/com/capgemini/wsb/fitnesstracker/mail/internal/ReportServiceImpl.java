package com.capgemini.wsb.fitnesstracker.mail.internal;

import com.capgemini.wsb.fitnesstracker.mail.api.EmailSender;
import com.capgemini.wsb.fitnesstracker.training.api.Training;
import com.capgemini.wsb.fitnesstracker.training.api.TrainingProvider;
import com.capgemini.wsb.fitnesstracker.user.api.User;
import com.capgemini.wsb.fitnesstracker.user.api.UserProvider;
import com.capgemini.wsb.fitnesstracker.user.internal.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ReportServiceImpl implements UserProvider {
    private final TrainingProvider trainingProvider;
    private final EmailSender emailSender;
    private final UserProvider userProvider;
    private final UserRepository userRepository;

    public ReportServiceImpl(TrainingProvider trainingProvider, EmailSender emailSender, UserProvider userProvider, UserRepository userRepository) {
        this.trainingProvider = trainingProvider;
        this.emailSender = emailSender;
        this.userProvider = userProvider;
        this.userRepository = userRepository;
    }
    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }


    @Scheduled(cron = "0 0 12 * * MON") // co piątek o 20:35
//    @Scheduled(cron = "*/15 * * * * *") // co 15 sekund
    public void sendReports() {
        List<User> users = userProvider.findAllUsers();

        for (User user : users) {
            List<Training> trainings = trainingProvider.getTrainingsByUserId(user.getId());
            emailSender.send(trainings);
        }
    }

    @Override
    public Optional<User> getUser(Long userId) {
        return Optional.empty();
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public Optional<User> getUserByFirstNameAndLastName(String firstname, String lastName) {
        return Optional.empty();
    }
}
