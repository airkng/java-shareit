package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.model.User;

public interface UserRepositoryDb extends JpaRepository<User, Long> {

    boolean existsById(long userId);
}
