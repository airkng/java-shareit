package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User get(Integer userId);

    List<User> getAll();

    User create(User user);

    User replace(User user, Integer userId);

    void delete(Integer userId);

    boolean contains(Integer userId);
}
