package com.capgemini.wsb.fitnesstracker.user.internal;

import com.capgemini.wsb.fitnesstracker.user.api.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Query searching users by email address. It matches by exact match.
     *
     * @param email email of the user to search
     * @return {@link Optional} containing found user or {@link Optional#empty()} if none matched
     */
    default Optional<User> findByEmail(String email) {
        return findAll().stream()
                        .filter(user -> Objects.equals(user.getEmail(), email))
                        .findFirst();
    }

    default Optional<User> findByNameSurname(String name, String surname) {
        return findAll().stream()
                .filter(user -> Objects.equals(user.getFirstName(), name)).filter(user -> Objects.equals(user.getLastName(), surname))
                .findFirst();
    }

    @Query("SELECT u FROM User u WHERE u.birthdate < :birthdate")
    List<User> findUsersOlderThan(@Param("birthdate")LocalDate birthdate);

}
