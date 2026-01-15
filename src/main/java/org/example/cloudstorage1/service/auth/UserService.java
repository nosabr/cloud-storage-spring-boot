package org.example.cloudstorage1.service.auth;

import lombok.RequiredArgsConstructor;
import org.example.cloudstorage1.dto.SignupRequest;
import org.example.cloudstorage1.dto.UserResponse;
import org.example.cloudstorage1.entity.User;
import org.example.cloudstorage1.exception.UserNotFoundException;
import org.example.cloudstorage1.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public User signUp(SignupRequest request) {
        String hashedPassword = passwordEncoder.encode(request.password());
        return userRepository.save(new User(request.username(), hashedPassword));
    }

    public User getUserByUsername(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if(userOpt.isEmpty()){
            throw new UserNotFoundException("User not found");
        }
        return userOpt.get();
    }

    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
}
