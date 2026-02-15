package com.event;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class EventReservationApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventReservationApplication.class, args);
    }
}

@RestController
class StartupCheckController {

    @GetMapping("/")
    public String root() {
        return "backend up";
    }

    @GetMapping("/api/health")
    public String health() {
        return "ok";
    }
}
