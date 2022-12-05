package ru.works.dont.touch.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        System.out.println("hello\n");
        SpringApplication.run(Main.class, args);
    }

}