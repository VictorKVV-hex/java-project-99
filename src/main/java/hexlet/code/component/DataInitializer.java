package hexlet.code.component;

import hexlet.code.model.Label;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.CustomUserDetailsService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import hexlet.code.model.User;
import java.util.Arrays;


@Component
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final TaskStatusRepository taskStatusRepository;
    private final UserRepository userRepository;
    private LabelRepository labelRepository;
    @Autowired
    private final CustomUserDetailsService userService;

    private PasswordEncoder encoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (userRepository.findByEmail("hexlet@example.com").isEmpty()) {
            var email = "hexlet@example.com";
            var userData = new User();
            userData.setEmail(email);
            userData.setPassword("qwerty");
            userService.createUser(userData);
        }

        var statusNames = Arrays.asList("draft", "to_review", "to_be_fixed", "to_publish", "published");
        var taskStatuses = statusNames.stream()
                .map(name -> {
                    var taskStatus = new TaskStatus();
                    taskStatus.setSlug(name);
                    taskStatus.setName(name);
                    taskStatusRepository.save(taskStatus);
                    return taskStatus;
                }).toList();

        var labelNames = Arrays.asList("bug", "feature");
        labelNames.stream()
                .map(name -> {
                    var label = new Label();
                    label.setName(name);
                    labelRepository.save(label);
                    return label;
                }).toList();

    }

}
