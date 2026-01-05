package org.example.cloudstorage1.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.cloudstorage1.service.auth.AuthService;
import org.example.cloudstorage1.service.auth.UserService;
import org.example.cloudstorage1.dto.*;
import org.example.cloudstorage1.service.storage.FolderStorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final FolderStorageService folderStorageService;

    public AuthController(AuthService authService, UserService userService, FolderStorageService folderStorageService) {
        this.authService = authService;
        this.userService = userService;
        this.folderStorageService = folderStorageService;
    }


    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@Valid @RequestBody LoginRequest request) {
        log.info("sign-in request: '{}'", request.toString());
        return ResponseEntity.ok().body(authService.signIn(request));
    }


    @PostMapping("/sign-up")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request) {
        log.info("sign-up request: {}", request.toString());
        if (userService.existsByUsername(request.username())) {
            log.warn("username already exists: '{}'", request.username());
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse(ErrorMessage.USERNAME_ALREADY_EXISTS));
        }
        UserResponse response = userService.signUp(request);
        //TODO Change service here
        folderStorageService.createFolder(response.username());
        log.info("sign-up request, User created: '{}'", response.toString());
        return ResponseEntity.ok().body(response);
    }
}
