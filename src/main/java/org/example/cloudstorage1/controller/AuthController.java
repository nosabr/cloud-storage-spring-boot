package org.example.cloudstorage1.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cloudstorage1.entity.User;
import org.example.cloudstorage1.exception.UsernameAlreadyExistsException;
import org.example.cloudstorage1.service.FileNodeService;
import org.example.cloudstorage1.service.FileSystemService;
import org.example.cloudstorage1.service.auth.AuthService;
import org.example.cloudstorage1.service.auth.UserService;
import org.example.cloudstorage1.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final FileNodeService fileNodeService;
    private final FileSystemService fileSystemService;


    @PostMapping("/sign-in")
    public ResponseEntity<UserResponse> signIn(@Valid @RequestBody LoginRequest request) {
        log.info("sign-in request: '{}'", request.username());
        return ResponseEntity.ok(authService.signIn(request));
    }

    @PostMapping("/sign-up")
    public ResponseEntity<UserResponse> signup(@Valid @RequestBody SignupRequest request) {
        log.info("sign-up request: {}", request.username());
        if (userService.existsByUsername(request.username())) {
            log.warn("username already exists: '{}'", request.username());
            throw new UsernameAlreadyExistsException(request.username());
        }

        User user = userService.signUp(request);

        UserResponse response = new UserResponse(user.getUsername());
        log.info("sign-up request, User created: '{}'", response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
