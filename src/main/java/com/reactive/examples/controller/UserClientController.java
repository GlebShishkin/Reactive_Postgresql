package com.reactive.examples.controller;

import com.reactive.examples.client.UserClient;
import com.reactive.examples.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// клиентский контроллер для обращения к "серверному" контроллеру UserController через WebClient
@RestController
@RequestMapping("/client/users")
public class UserClientController {

    @Autowired
    private UserClient userClient;

    // получить пользователя через WebClient
    @GetMapping("/{userId}")
    public Mono<ResponseEntity<User>> getUserById(@PathVariable String userId) {
        Mono<User> user = userClient.getUser(userId);
        return user.map(u -> ResponseEntity.ok(u))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    // получить всех пользователей через WebClient
    @GetMapping
    public Flux<User> getAllUsers() {
        return userClient.getAllUsers();
    }

    // создать пользователя через WebClient
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<User> create(@RequestBody User user) {
        return userClient.createUser(user);
    }
}
