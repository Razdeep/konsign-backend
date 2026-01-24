package com.razdeep.konsignapi;

import java.util.TimeZone;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
@EnableCaching
public class KonsignApiApplication implements CommandLineRunner {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
        SpringApplication.run(KonsignApiApplication.class, args);
    }

    @Override
    public void run(String... args) {}
}
