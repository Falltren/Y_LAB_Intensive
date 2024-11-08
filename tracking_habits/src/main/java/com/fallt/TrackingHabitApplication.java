package com.fallt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class TrackingHabitApplication {
    public static void main(String[] args) {
        SpringApplication.run(TrackingHabitApplication.class, args);
    }
}
