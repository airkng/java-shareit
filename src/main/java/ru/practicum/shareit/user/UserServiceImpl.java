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
    private final UserRepositoryDb userRepository;
    private final UserMapper mapper;

    @Override
    public UserDto get(final Long userId) {
        return mapper.toUserDto(userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException(String.format("User with %d user id not found", userId));
        }));

    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(mapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto create(final UserCreationDto userCreationDto) {
        User user = mapper.toUser(userCreationDto);
        return mapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto update(final UserCreationDto userCreationDto, final Long userId) {
        User userInfo = mapper.toUser(userCreationDto);
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User oldUser = userOptional.get();
            if (userInfo.getName() != null) {
                oldUser.setName(userInfo.getName());
            }
            if (userInfo.getEmail() != null) {
                oldUser.setEmail(userInfo.getEmail());
            }
            return mapper.toUserDto(userRepository.save(oldUser));
        } else {
            throw new NotFoundException(String.format("user id %d not found", userId));
        }
    }

    @Override
    public void delete(final Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public boolean contains(final Long userId) {
        return userRepository.existsById(userId);
    }

}
