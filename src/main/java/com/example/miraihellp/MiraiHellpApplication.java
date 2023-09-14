package com.example.miraihellp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = {"com.example.miraihellp"})
@EnableAsync
public class MiraiHellpApplication {

    public static void main(String[] args) {
        SpringApplication.run(MiraiHellpApplication.class, args);
    }

}
