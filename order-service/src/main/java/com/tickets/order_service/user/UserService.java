package com.tickets.order_service.user;

import org.springframework.stereotype.Service;
import java.util.List;

import com.tickets.order_service.auth.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    public UserService(UserRepository repository, PasswordEncoder encoder, JwtService jwtService) {
        this.repository = repository;
        this.encoder = encoder;
        this.jwtService = jwtService;
    }

    public User register(String email, String name, String password) {
        if (repository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already registered: " + email);
        }
        // public registration always creates a USER — never an admin
        return repository.save(new User(email, name, encoder.encode(password), User.Role.USER));
    }

    public String login(String email, String password) {
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        if (!encoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        return jwtService.generateToken(user.getId(), user.getEmail(), user.getRole().name());
    }

    public List<User> findAll() { return repository.findAll(); }
}