package org.example.cloudstorage1.controller;

import jakarta.validation.Valid;
import org.example.cloudstorage1.Service.AuthService;
import org.example.cloudstorage1.Service.UserService;
import org.example.cloudstorage1.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }


    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@RequestBody LoginRequest request) {
        return ResponseEntity.ok().body(authService.signIn(request));
    }


    @PostMapping("/sign-up")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request) {
        if (userService.existsByUsername(request.username())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse(ErrorMessage.USERNAME_ALREADY_EXISTS));
        }
        UserResponse response = userService.signUp(request);
        return ResponseEntity.ok().body(response);
    }
}
