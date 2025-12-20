package org.example.cloudstorage1.controller;

import jakarta.validation.Valid;
import org.example.cloudstorage1.Service.UserService;
import org.example.cloudstorage1.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request){
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(request.username(),request.password());
        Authentication authentication = authenticationManager.authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return ResponseEntity.ok().body(new UserResponse(authentication.getName()));
    }


    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request) {
        if (userService.existsByUsername(request.username())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse(ErrorMessage.USERNAME_ALREADY_EXISTS));
        }

        if (userService.existsByEmail(request.email())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse(ErrorMessage.EMAIL_ALREADY_EXISTS));
        }
        UserResponse response = userService.createUser(request);
        return ResponseEntity.ok().body(response);
    }
}
