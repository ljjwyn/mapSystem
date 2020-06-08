package com.lijiajie.mapsystem.trajectory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @describe 开启EnableScheduling
 */
@SpringBootApplication
@EnableScheduling
public class TrajectorySendApplication {
    public static void main(String[] args) {
        SpringApplication.run(TrajectorySendApplication.class, args);
    }
}
