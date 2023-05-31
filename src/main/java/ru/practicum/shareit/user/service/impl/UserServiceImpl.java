package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User get(Integer userId) {
        Optional<User> user = userRepository.get(userId);

        if(user.isEmpty()) {
            throw new UserNotFoundException(String.format("User with %d user id not found", userId));
        }
        return user.get();
    }

    @Override
    public List<User> getAll() {
        Optional<List<User>> users = userRepository.getAll();
        if(users.isEmpty()) {
            return List.of();
        } else {
            return users.get();
        }
    }

    @Override
    public User create(User user) {
        return userRepository.create(user);
    }

    @Override
    public User replace(User user, Integer userId) {
        return userRepository.replace(user, userId);
    }

    @Override
    public void delete(Integer userId) {
        userRepository.delete(userId);
    }
}
