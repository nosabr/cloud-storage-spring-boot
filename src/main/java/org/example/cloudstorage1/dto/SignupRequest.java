package org.example.cloudstorage1.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignupRequest(
        @NotBlank(message = "Username is required")
        @Size(min = 4, max = 12, message = "Username must be between 4 and 12 characters long")
        @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers and underscore")
        String username,

        @NotBlank(message = "Password is required")
        @Size(min = 4, max = 20, message = "Password must be between 4 and 20 characters long")
        String password
) {}
