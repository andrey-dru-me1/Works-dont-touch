package ru.works.dont.touch.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AccessingDataMysqlApplication {
    public static void main(String[] args) {
        System.out.println("hello\n");
        SpringApplication.run(AccessingDataMysqlApplication.class, args);
    }

}