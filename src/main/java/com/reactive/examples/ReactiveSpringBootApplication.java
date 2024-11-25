package com.reactive.examples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// взято из https://github.com/sumanentc/spring-webflux-reactive-rest-api-example/tree/master/src
// пример:
// 1) взаимодействия реативного приложения с postgresql
// 2) создание и заполнение таблиц в схеме с помощью sql-скрипта
// 3) естирование реактичного приложения
// 4) Swagger не заработал!!!

@SpringBootApplication
public class ReactiveSpringBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReactiveSpringBootApplication.class, args);
	}

}
