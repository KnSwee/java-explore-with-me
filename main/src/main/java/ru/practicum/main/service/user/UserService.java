package ru.practicum.main.service.user;

import jakarta.validation.Valid;
import ru.practicum.main.dto.user.NewUserRequest;
import ru.practicum.main.dto.user.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers(List<Long> ids, Integer from, Integer size);

    UserDto createUser(@Valid NewUserRequest newUserRequest);

    void deleteUser(Long userId);
}
