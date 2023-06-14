package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.EmailAlreadyExist;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserRepository {

    private final HashMap<Integer, User> users = new HashMap<>();

    private final HashSet<String> emails = new HashSet<>();

    public Optional<User> get(final Integer userId) {
        return Optional.ofNullable(users.get(userId));
    }

    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    public User create(final User user) {
        checkEmail(user.getEmail());
        users.put(user.getId(), user);
        emails.add(user.getEmail());
        return user;
    }

    public User update(final User user, final Integer userId) {
        users.replace(userId, user);
        return user;
    }

    public void delete(final Integer userId) {
        User user = users.remove(userId);
        emails.remove(user.getEmail());

    }

    public boolean contains(final Integer userId) {
        return users.containsKey(userId);
    }

    public boolean checkEmail(final String email) {
        if (emails.contains(email)) {
            throw new EmailAlreadyExist(String.format("%s email already exists", email));
        } else {
            return true;
        }
    }

    public boolean updateEmail(final String oldVal, final String newVal) {
        emails.remove(oldVal);
        checkEmail(newVal);
        emails.add(newVal);
        return true;
    }

}
