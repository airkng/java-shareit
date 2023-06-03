package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreationDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static Integer userIdCount = 1;
    private final UserRepository userRepository;
    private final UserMapper mapper;

    @Override
    public UserDto get(final Integer userId) {
        return mapper.toUserDto(userRepository.get(userId).orElseThrow(() -> {
            throw new NotFoundException(String.format("User with %d user id not found", userId));
        }));

    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.getAll().stream()
                .map(mapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto create(final UserCreationDto userCreationDto) {
        User user = mapper.toUser(userCreationDto);
        user.setId(userIdCount);
        userRepository.checkEmail(user.getEmail());
        increaseUserId();
        return mapper.toUserDto(userRepository.create(user));
    }

    @Override
    public UserDto update(final UserCreationDto userCreationDto, final Integer userId) {
        User userInfo = mapper.toUser(userCreationDto);
        Optional<User> userOptional = userRepository.get(userId);
        if (userOptional.isPresent()) {
            User oldUser = userOptional.get();
            if (userInfo.getName() != null) {
                oldUser.setName(userInfo.getName());
            }
            if (userInfo.getEmail() != null) {
                String oldEmail = oldUser.getEmail();
                if (!oldEmail.equals(userInfo.getEmail())) {
                    userRepository.updateEmail(oldEmail, userInfo.getEmail());
                    oldUser.setEmail(userInfo.getEmail());
                }
            }
            return mapper.toUserDto(userRepository.update(oldUser, userId));
        } else {
            throw new NotFoundException(String.format("user id %d not found", userId));
        }
    }

    @Override
    public void delete(final Integer userId) {
        userRepository.delete(userId);
    }

    @Override
    public boolean contains(final Integer userId) {
        return userRepository.contains(userId);
    }

    private static void increaseUserId() {
        userIdCount++;
    }
}
