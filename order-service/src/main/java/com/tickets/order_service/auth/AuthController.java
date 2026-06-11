package com.tickets.order_service.auth;

import com.tickets.order_service.user.UserService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> body) {
        String token = userService.login(body.get("email"), body.get("password"));
        return Map.of("token", token);
    }
}