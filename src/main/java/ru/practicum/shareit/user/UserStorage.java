package ru.practicum.shareit.user;

import java.util.List;

public interface UserStorage {
    User add(User user);

    User getById(int id);

    User getByEmail(String email);

    List<User> getAll();

    void update(User user);

    void delete(int id);
}
