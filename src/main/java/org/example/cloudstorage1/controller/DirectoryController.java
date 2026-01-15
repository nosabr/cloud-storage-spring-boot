package org.example.cloudstorage1.controller;

import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cloudstorage1.dto.ResourceResponse;
import org.example.cloudstorage1.entity.FileNode;
import org.example.cloudstorage1.entity.FileType;
import org.example.cloudstorage1.entity.User;
import org.example.cloudstorage1.exception.UserNotFoundException;
import org.example.cloudstorage1.service.DirectoryService;
import org.example.cloudstorage1.service.auth.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/api/directory")
@RequiredArgsConstructor
public class DirectoryController {

    private final UserService userService;
    private final DirectoryService directoryService;

    @GetMapping
    public void getDirectory() {
        // path checked through @VALID in record
    }

    @PostMapping
    public ResponseEntity<ResourceResponse> createNewDirectory(
            @RequestParam @Pattern(regexp = ".*/") String path,
            Principal principal) {

        Optional<User> userOpt = userService.getUserByUsername(principal.getName());
        if(userOpt.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }
        FileNode fileNode = directoryService.createDirectory(userOpt.get(), path);
        ResourceResponse response = new ResourceResponse(
                fileNode.getPath(), fileNode.getName(),null, FileType.DIRECTORY);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
