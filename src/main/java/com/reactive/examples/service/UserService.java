package com.reactive.examples.service;

import com.reactive.examples.dto.UserDepartmentDTO;
import com.reactive.examples.model.Department;
import com.reactive.examples.repository.DepartmentRepository;
import com.reactive.examples.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.reactive.examples.model.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.function.BiFunction;

@Service
@Slf4j
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    public Mono<User> createUser(User user) {
        return userRepository.save(user);
    }

    public Flux<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Mono<User> findById(Integer userId) {
        return userRepository.findById(userId);
    }

    public Mono<User> updateUser(Integer userId, User user) {
        return userRepository.findById(userId)
                .flatMap(
                    dbUser -> {
                        dbUser.setAge(user.getAge());
                        dbUser.setSalary(user.getSalary());
                        return userRepository.save(dbUser);
                    }
                );
    }

    public Mono<User> deleteUser(Integer userId) {
        return userRepository.findById(userId)
                // flatMap(Mono<User>)
                .flatMap(existingUser ->
                        // Mono.delete(T entity)
                        userRepository.delete(existingUser)
                                .then(Mono.just(existingUser))  // возвращаем в потоке удаленного User-a
                );
    }

    public Flux<User> findUserByAge(int age) {
        return userRepository.findByAge(age);
    }

    // выбрать User по списку (List userIds) в несколько потоков
    public Flux<User> fetchUsers(List<Integer> userIds) {
        return Flux.fromIterable(userIds)
                .parallel()
                .runOn(Schedulers.boundedElastic())
                .flatMap(i -> findById(i))
                .ordered((u1, u2) -> u2.getId() - u1.getId());
    }

    private Mono<Department> getDepartmentByUserId(Integer userId){
        return departmentRepository.findByUserId(userId);
    }

    // определяем функциональный интерфейс типа BiFunction, кот. будем использовать ниже для формирования DTO "UserDepartmentDTO"
    // (Mono.zip - это функциональный интерфейс типа типа BiFunction)
    // цель данного BiFunction - на основании 2-х стримов создать DTO, используя builder вместо конструктора
    private BiFunction<User, Department, UserDepartmentDTO> userDepartmentDTOBiFunction
            // используем св-во @Builder у UserDepartmentDTO -> постепенно создаем объект, вместо вызова конструктора
            = (x1, x2) -> UserDepartmentDTO.builder()
            .age(x1.getAge())
            .departmentId(x2.getId())
            .departmentName(x2.getName())
            .userName(x1.getName())
            .userId(x1.getId())
            .loc(x2.getLoc())
            .salary(x1.getSalary()).build();

    // получение DTO (User + Department) по userId
    public Mono<UserDepartmentDTO> fetchUserAndDepartment(Integer userId) {
        Mono<User> user = findById(userId).subscribeOn(Schedulers.boundedElastic());
        Mono<Department> department = getDepartmentByUserId(userId).subscribeOn(Schedulers.boundedElastic());
        // Mono.zip - это функциональный интерфейс типа типа BiFunction
        // в функциональный интерфейс типа BiFunction подставляем "userDepartmentDTOBiFunction" третьим параметром
        // для объединения двух потоков
        return Mono.zip(user, department, userDepartmentDTOBiFunction);
    }


}
