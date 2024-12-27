package ru.practicum.main.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.dto.user.NewUserRequest;
import ru.practicum.main.dto.user.UserDto;
import ru.practicum.main.exception.DataConflictException;
import ru.practicum.main.exception.ElementNotFoundException;
import ru.practicum.main.model.User;
import ru.practicum.main.repository.UserRepository;
import ru.practicum.main.service.user.mapper.UserDtoMapper;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        List<User> users;
        if (ids == null || ids.isEmpty()) {
            PageRequest page = PageRequest.of(from / size, size, Sort.by("id").ascending());
            users = userRepository.findAll(page).getContent();
        } else {
            users = userRepository.findAllByIdIn(ids);
        }
        return UserDtoMapper.toUserDto(users);
    }

    @Override
    public UserDto createUser(NewUserRequest newUserRequest) {
        if (userRepository.existsByEmail(newUserRequest.getEmail())) {
            throw new DataConflictException("Пользователь с таким email уже существует");
        }
        User user = userRepository.save(UserDtoMapper.toUser(newUserRequest));
        return UserDtoMapper.toUserDto(user);
    }

    @Override
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ElementNotFoundException("Пользователь с id " + userId + " не найден");
        }
        userRepository.deleteById(userId);
    }

}
