package com.capgemini.wsb.fitnesstracker.user.api;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Interface (API) for modifying operations on {@link User} entities through the API.
 * Implementing classes are responsible for executing changes within a database transaction, whether by continuing an existing transaction or creating a new one if required.
 */
public interface UserService {

    User createUser(User user);

    Optional<User> getUser(final Long userId);

    User getUserById(Long userId);

    void deleteUser(Long userId);

    User updateUser(Long userId, User user);

    List<User> findUsersOlderThanByDate(LocalDate date);

}
