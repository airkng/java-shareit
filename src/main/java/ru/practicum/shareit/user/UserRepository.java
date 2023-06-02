package ru.practicum.shareit.user;

import lombok.Getter;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.exception.EmailAlreadyExist;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserRepository {
    private static Integer USER_ID = 1;

    @Getter
    private final HashMap<Integer, User> users = new HashMap<>();

    private final HashSet<String> emails = new HashSet<>();

    public Optional<User> get(Integer userId) {
        return Optional.of(users.get(userId));
    }

    public Optional<List<User>> getAll() {
        return Optional.of(new ArrayList<>(users.values()));
    }

    public User create(User user) {
        checkEmail(user.getEmail());
        user.setId(USER_ID);
        users.put(USER_ID, user);
        emails.add(user.getEmail());
        USER_ID++;
        return user;
    }

    public User replace(User userInfo, Integer userId) {
        if (users.containsKey(userId)) {
            User user = users.get(userId);
            if (userInfo.getName() != null) {
                user.setName(userInfo.getName());
            }
            if (userInfo.getEmail() != null) {
                String oldEmail = user.getEmail();
                if (!oldEmail.equals(userInfo.getEmail())) {
                    updateEmail(oldEmail, userInfo.getEmail());
                    user.setEmail(userInfo.getEmail());
                }
            }
            users.replace(userId, user);
            return user;
        } else {
            throw new UserNotFoundException(String.format("user id %d not found", userId));
        }
    }

    public void delete(Integer userId) {
        User user = users.remove(userId);
        emails.remove(user.getEmail());

    }

    private boolean checkEmail(String email) {
        if (emails.contains(email)) {
            throw new EmailAlreadyExist(String.format("%s email already exists", email));
        } else {
            return true;
        }
    }

    private boolean updateEmail(String oldVal, String newVal) {
        emails.remove(oldVal);
        checkEmail(newVal);
        emails.add(newVal);
        return true;
    }

    public boolean contains(Integer userId) {
        return users.containsKey(userId);
    }
}
