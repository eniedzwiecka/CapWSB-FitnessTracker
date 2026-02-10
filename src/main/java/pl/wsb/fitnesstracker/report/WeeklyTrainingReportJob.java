package pl.wsb.fitnesstracker.report;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.wsb.fitnesstracker.training.api.Training;
import pl.wsb.fitnesstracker.training.api.TrainingRepository;
import pl.wsb.fitnesstracker.user.api.User;
import pl.wsb.fitnesstracker.user.internal.UserRepository;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Component
public class WeeklyTrainingReportJob {

    private final UserRepository userRepository;
    private final TrainingRepository trainingRepository;

    public WeeklyTrainingReportJob(UserRepository userRepository, TrainingRepository trainingRepository) {
        this.userRepository = userRepository;
        this.trainingRepository = trainingRepository;
    }

    @Scheduled(cron = "0 0 9 * * MON")
    public void printWeeklyReportToConsole() {
        LocalDate today = LocalDate.now();

        LocalDate weekStart = today.with(DayOfWeek.MONDAY);
        LocalDate weekEnd = today.with(DayOfWeek.SUNDAY);

        Instant weekStartInstant = weekStart.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant weekEndInstant = weekEnd.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

        List<User> users = userRepository.findAll();

        System.out.println("=======================================");
        System.out.println("WEEKLY TRAINING REPORT");
        System.out.println("Week: " + weekStart + " - " + weekEnd);
        System.out.println("Users: " + users.size());
        System.out.println("=======================================");

        for (User user : users) {
            List<Training> trainings = trainingRepository.findAllByUser_Id(user.getId());

            long trainingsThisWeek = trainings.stream()
                    .filter(t -> t.getStartTime() != null)
                    .map(t -> t.getStartTime().toInstant())
                    .filter(d -> !d.isBefore(weekStartInstant) && d.isBefore(weekEndInstant))
                    .count();

            System.out.println("- " + user.getFirstName() + " " + user.getLastName()
                    + " | trainings this week: " + trainingsThisWeek);
        }

        System.out.println("=======================================");
    }
}