package ru.practicum.main.service.user.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.main.dto.user.NewUserRequest;
import ru.practicum.main.dto.user.UserDto;
import ru.practicum.main.dto.user.UserShortDto;
import ru.practicum.main.model.User;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserDtoMapper {

    public static UserDto toUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getUsername());
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    public static List<UserDto> toUserDto(List<User> users) {
        List<UserDto> userDtos = new ArrayList<>();
        for (User user : users) {
            userDtos.add(toUserDto(user));
        }
        return userDtos;
    }

    public static User toUser(NewUserRequest newUserRequest) {
        User user = new User();
        user.setUsername(newUserRequest.getName());
        user.setEmail(newUserRequest.getEmail());
        return user;
    }

    public static User toUser(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setUsername(userDto.getName());
        user.setEmail(userDto.getEmail());
        return user;
    }

    public static User toUser(UserShortDto userShortDto) {
        User user = new User();
        user.setId(userShortDto.getId());
        user.setUsername(userShortDto.getName());
        return user;
    }

    public static UserShortDto toUserShortDto(User user) {
        UserShortDto userShortDto = new UserShortDto();
        userShortDto.setId(user.getId());
        userShortDto.setName(user.getUsername());
        return userShortDto;
    }

}
