package com.fallt;

import com.fallt.audit_starter.aop.EnableAudit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAudit
public class TrackingHabitApplication {
    public static void main(String[] args) {
        SpringApplication.run(TrackingHabitApplication.class, args);
    }
}
