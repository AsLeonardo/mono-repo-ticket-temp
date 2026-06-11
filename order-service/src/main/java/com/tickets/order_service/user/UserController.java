package com.tickets.order_service.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<User> register(@RequestBody Map<String, String> body) {
        User user = service.register(body.get("email"), body.get("name"), body.get("password"));
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @GetMapping
    public List<User> findAll() {
        return service.findAll();
    }
}