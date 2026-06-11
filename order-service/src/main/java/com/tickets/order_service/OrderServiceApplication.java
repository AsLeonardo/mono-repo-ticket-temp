package com.tickets.order_service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import com.tickets.order_service.user.UserRepository;
import com.tickets.order_service.user.User;

@SpringBootApplication
@EnableScheduling
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public org.springframework.security.crypto.password.PasswordEncoder passwordEncoder() {
    return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
    }

    @Bean
    CommandLineRunner seedAdmin(UserRepository repo,
            org.springframework.security.crypto.password.PasswordEncoder encoder) {
        return args -> {
            if (repo.findByEmail("admin@tickets.com").isEmpty()) {
                repo.save(new User("admin@tickets.com", "Admin",
                        encoder.encode("admin123"), User.Role.ADMIN));
            }
        };
    }
}