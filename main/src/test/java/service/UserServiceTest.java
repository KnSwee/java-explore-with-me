package service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.MainApplication;
import ru.practicum.main.dto.user.NewUserRequest;
import ru.practicum.main.dto.user.UserDto;
import ru.practicum.main.model.User;
import ru.practicum.main.repository.UserRepository;
import ru.practicum.main.service.user.UserService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(
        classes = MainApplication.class,
        properties = "jdbc.url=jdbc:postgresql://localhost:5432/test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private final UserService userService;
    private final UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void createTest() {
        UserDto created = userService.createUser(new NewUserRequest("created@example.com", "Created"));
        User first = userRepository.findAll().getFirst();

        assertThat(created.getEmail(), equalTo(first.getEmail()));
        assertThat(created.getName(), equalTo(first.getUsername()));
    }

    @Test
    void getTest() {
        UserDto created = userService.createUser(new NewUserRequest("created@example.com", "Created"));
        UserDto created2 = userService.createUser(new NewUserRequest("created2@example.com", "Created2"));

        List<UserDto> users = userService.getUsers(List.of(created.getId(), created2.getId()), 0, 10);

        assertThat(users.size(), equalTo(2));
        assertThat(users.get(0).getEmail(), equalTo(created.getEmail()));
        assertThat(users.get(1).getEmail(), equalTo(created2.getEmail()));
    }

    @Test
    void deleteTest() {
        UserDto created = userService.createUser(new NewUserRequest("created@example.com", "Created"));
        userService.deleteUser(created.getId());
        List<User> users = userRepository.findAll();
        assertThat(users.size(), equalTo(0));
    }

}
