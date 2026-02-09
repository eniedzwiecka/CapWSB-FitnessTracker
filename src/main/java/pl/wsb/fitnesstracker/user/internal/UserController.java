package pl.wsb.fitnesstracker.user.internal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.wsb.fitnesstracker.user.api.User;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/v1/users")
class UserController {

    private final UserRepository userRepository;

    UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    List<User> getAll() {
        return userRepository.findAll();
    }

    @GetMapping("/simple")
    List<SimpleUserDto> getAllSimple() {
        return userRepository.findAll().stream()
                .map(u -> new SimpleUserDto(u.getFirstName(), u.getLastName()))
                .toList();
    }

    @GetMapping("/{id}")
    User getById(@PathVariable Long id) {
        return userRepository.findById(id).orElseThrow();
    }

    @GetMapping("/email")
    List<UserEmailDto> getByEmail(@RequestParam String email) {
        return userRepository.findByEmailContainingIgnoreCase(email).stream()
                .map(u -> new UserEmailDto(u.getId(), u.getEmail()))
                .toList();
    }

    @GetMapping("/older/{time}")
    List<User> getOlderThan(@PathVariable LocalDate time) {
        return userRepository.findAllByBirthdateBefore(time);
    }

    @DeleteMapping("/{userId}")
    ResponseEntity<Void> delete(@PathVariable Long userId) {
        userRepository.deleteById(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    ResponseEntity<Void> create(@RequestBody UserRequest req) {
        User user = new User(req.firstName(), req.lastName(), req.birthdate(), req.email());
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{userId}")
    ResponseEntity<Void> update(@PathVariable Long userId, @RequestBody UserRequest req) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setFirstName(req.firstName());
        user.setLastName(req.lastName());
        user.setBirthdate(req.birthdate());
        user.setEmail(req.email());
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    record SimpleUserDto(String firstName, String lastName) {}
    record UserEmailDto(Long id, String email) {}
    record UserRequest(String firstName, String lastName, LocalDate birthdate, String email) {}
}