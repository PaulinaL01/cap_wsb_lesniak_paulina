package com.capgemini.wsb.fitnesstracker.mail.internal;

import com.capgemini.wsb.fitnesstracker.mail.api.EmailDto;
import com.capgemini.wsb.fitnesstracker.mail.api.EmailSender;
import com.capgemini.wsb.fitnesstracker.training.api.Training;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
public class EmailSenderImpl implements EmailSender {
    private final MailProperties mailProperties;
    private final JavaMailSender mailSender;


    public EmailSenderImpl(MailProperties mailProperties, JavaMailSender mailSender) {
        this.mailProperties = mailProperties;
        this.mailSender = mailSender;
    }

    @Override
    public void send(List<Training> email) {
        String senderAddress = "sandbox.smtp.mailtrap.io";
        String title = "Your Report";
        String inside = generateEmail(email);

        EmailDto emailDto = new EmailDto(senderAddress, title, inside);
        sendReport(emailDto);
    }

    private void sendReport(EmailDto emailDto) {
        try {
            MimeMessage mimeMsg = mailSender.createMimeMessage();
            MimeMessageHelper msg = new MimeMessageHelper(mimeMsg, true);
            msg.setFrom(mailProperties.getFrom());
            msg.setSubject(emailDto.subject());
            msg.setText(emailDto.content(), true);
            msg.setTo(emailDto.toAddress());

            mailSender.send(mimeMsg);
        } catch (MessagingException e) {
            System.err.println("An error during sending message: " + e.getMessage());
        }
    }

    private String generateEmail(List<Training> trainings) {
        StringBuilder text = new StringBuilder("Your Report\n\n");

        for (Training training : trainings) {
            Instant startTimeInstant = training.getStartTime().toInstant();
            Instant endTimeInstant = training.getEndTime().toInstant();
            Duration duration = Duration.between(startTimeInstant, endTimeInstant);
            long hours = duration.toHours();
            long minutes = duration.minusHours(hours).toMinutes();
            text.append("Duration: ").append(hours).append(" hours, ").append(minutes).append(" minutes").append("\n\n")
                .append("Activity Type: ").append(training.getActivityType()).append("\n")
                .append("Distance: ").append(training.getDistance()).append("\n")
                .append("Average Speed: ").append(training.getAverageSpeed()).append("\n\n");
        }
        return text.toString();
    }
}
