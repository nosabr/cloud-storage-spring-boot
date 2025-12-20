package org.example.cloudstorage1.Service;

import org.example.cloudstorage1.dto.SignupRequest;
import org.example.cloudstorage1.dto.UserResponse;
import org.example.cloudstorage1.entity.User;
import org.example.cloudstorage1.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse signUp(SignupRequest request) {
        String hashedPassword = passwordEncoder.encode(request.password());
        User user = userRepository.save(new User(request.username(), hashedPassword));
        return new UserResponse(user.getUsername());
    }

    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
}
