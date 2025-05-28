package com.example.application.service;

import com.example.application.entity.User;
import com.example.application.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepo;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public void register(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        user.setBlocked(false);
        userRepo.save(user);
    }

    /** Проверяем email+пароль, возвращаем пользователя, если всё ОК */
    public Optional<User> authenticate(String email, String rawPassword) {
        return userRepo
                .findByEmail(email)
                .filter(u -> encoder.matches(rawPassword, u.getPassword()));
    }
}
