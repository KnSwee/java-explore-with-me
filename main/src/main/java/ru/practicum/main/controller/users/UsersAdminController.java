package ru.practicum.main.controller.users;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.user.NewUserRequest;
import ru.practicum.main.dto.user.UserDto;
import ru.practicum.main.service.user.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
public class UsersAdminController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(name = "ids", required = false) List<Long> ids,
                                  @RequestParam(name = "from", defaultValue = "0") Integer from,
                                  @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return userService.getUsers(ids, from, size);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public UserDto createUser(@Valid @RequestBody NewUserRequest newUserRequest) {
        return userService.createUser(newUserRequest);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable(name = "userId") Long userId) {
        userService.deleteUser(userId);
    }

}
