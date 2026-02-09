package pl.wsb.fitnesstracker.user.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.wsb.fitnesstracker.user.internal.UserRepository;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/v1/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @GetMapping("/simple")
    public List<SimpleUserDto> getAllSimple() {
        return userRepository.findAll().stream()
                .map(u -> new SimpleUserDto(u.getFirstName(), u.getLastName()))
                .toList();
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable Long id) {
        return userRepository.findById(id).orElseThrow();
    }

    @GetMapping("/email")
    public List<UserEmailDto> getByEmail(@RequestParam String email) {
        return userRepository.findByEmail(email)
                .map(u -> List.of(new UserEmailDto(u.getId(), u.getEmail())))
                .orElseGet(List::of);
    }

    @GetMapping("/older/{time}")
    public List<User> getOlderThan(@PathVariable LocalDate time) {
        return userRepository.findAllByBirthdateBefore(time);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable Long userId) {
        userRepository.deleteById(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody UserRequest req) {
        User user = new User(req.firstName(), req.lastName(), req.birthdate(), req.email());
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Void> update(@PathVariable Long userId, @RequestBody UserRequest req) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setFirstName(req.firstName());
        user.setLastName(req.lastName());
        user.setBirthdate(req.birthdate());
        user.setEmail(req.email());
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    public record SimpleUserDto(String firstName, String lastName) {}
    public record UserEmailDto(Long id, String email) {}

    public record UserRequest(String firstName, String lastName, LocalDate birthdate, String email) {}
}
