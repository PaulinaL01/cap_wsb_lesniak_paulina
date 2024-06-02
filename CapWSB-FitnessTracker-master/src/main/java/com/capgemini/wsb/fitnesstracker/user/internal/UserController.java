package com.capgemini.wsb.fitnesstracker.user.internal;

import com.capgemini.wsb.fitnesstracker.user.api.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/v1/users")
class UserController {

    @Autowired
    private final UserServiceImpl userService;

    @Autowired
    private final UserMapper userMapper;

    UserController(UserServiceImpl userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody UserDto userDto) throws InterruptedException{


        System.out.println("User with e-mail: " + userDto.email() + "passed to the request");

        User user = userService.createUser(this.userMapper.toEntity(userDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(user);

    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.findAllUsers()
                          .stream()
                          .map(userMapper::toDto)
                          .toList();
    }

    @GetMapping("/simple")
    public ResponseEntity<List<UserSimpleDto>> getAllSimpleUsers() {
        List<UserSimpleDto> userSimpleDtos = userService.findAllUsers()
                .stream()
                .map(userMapper::toSimpleDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userSimpleDtos);
    }
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long userId) {
        return userService.getUser(userId)
                .map(userMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @GetMapping("/{name}/{surname}")
    public ResponseEntity<UserDto> getUser(@PathVariable String name, @PathVariable String surname) {
        String fullName = name + ' ' + surname;
        System.out.println(fullName);
        return userService.getUserByFirstNameAndLastName(name, surname)
                .map(userMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email")
    public ResponseEntity<List<User>> getUserByEmail(@RequestParam String email) {
        return userService.getUserByEmail(email)
                .map(Collections::singletonList)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/older/{date}")
    public ResponseEntity<List<UserDto>> getUsersOlderThan(@PathVariable LocalDate date) {
        return ResponseEntity.ok(userService.findUsersOlderThanByDate(date).stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList()));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        return ResponseEntity.ok(userMapper.toDto(userService.updateUser(userId, userMapper.toEntity(userDto))));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        if (userService.getUser(userId)!= null) {
            userService.deleteUser(userId);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}