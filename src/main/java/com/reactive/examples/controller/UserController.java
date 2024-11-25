package com.reactive.examples.controller;

import com.reactive.examples.dto.UserDepartmentDTO;
import com.reactive.examples.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.reactive.examples.model.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

// серверный контроллер, которому обращается клиентский контроллер "UserClientController" через WebClient
@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    // создание User
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<User> create(@RequestBody User user) {
log.info("1) ########### create");
        return userService.createUser(user);
    }

    @GetMapping
    public Flux<User> getAllUsers(){
        return userService.getAllUsers();
    }

    // получение ser по id
    @GetMapping("/{userId}")
    public Mono<ResponseEntity<User>> getUserById(@PathVariable Integer userId) {
        log.info("3) ########### getUserById");
        Mono<User> user = userService.findById(userId);
        return user.map(u -> ResponseEntity.ok(u))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{userId}")
    public Mono<ResponseEntity<User>> updateUserById(@PathVariable Integer userId, @RequestBody User user) {
        log.info("4) ########### updateUserById: userId = " + userId);
        return userService.updateUser(userId, user)
                .map(updateUser -> ResponseEntity.ok(updateUser))
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/{userId}")
    public Mono<ResponseEntity<Void>> deleteUserById(@PathVariable Integer userId) {
        return userService.deleteUser(userId)
                .map(r -> ResponseEntity.ok().<Void>build())
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/age/{age}")
    public Flux<User> getUsersByAge(@PathVariable int age) {
        return userService.findUserByAge(age);
    }

    // получение ользоватеей по списку
    @GetMapping("/search/id")
    public Flux<User> fetchUsersByIds(@RequestBody List<Integer> ids) {
        return userService.fetchUsers(ids);
    }

    @GetMapping("/{userId}/department")
    public Mono<UserDepartmentDTO> fetchUserAndDepartment(@PathVariable Integer userId) {
        return userService.fetchUserAndDepartment(userId);
    }

}
