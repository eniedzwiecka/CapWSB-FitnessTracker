package pl.wsb.fitnesstracker.training.internal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.wsb.fitnesstracker.training.api.Training;
import pl.wsb.fitnesstracker.training.api.TrainingRepository;

import java.util.List;

@RestController
@RequestMapping("/v1/trainings")
public class TrainingController {

    private final TrainingRepository trainingRepository;

    public TrainingController(TrainingRepository trainingRepository) {
        this.trainingRepository = trainingRepository;
    }

    @GetMapping
    public List<Training> getAll() {
        return trainingRepository.findAll();
    }

    @GetMapping("/{userId}")
    public List<Training> getAllForUser(@PathVariable Long userId) {
        return trainingRepository.findAllByUser_Id(userId);
    }
}



