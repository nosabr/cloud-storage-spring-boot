package org.example.cloudstorage1.controller;

import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cloudstorage1.entity.FileNode;
import org.example.cloudstorage1.entity.User;
import org.example.cloudstorage1.service.FileSystemService;
import org.example.cloudstorage1.service.auth.UserService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/api/directory")
@RequiredArgsConstructor
public class DirectoryController {

    private final UserService userService;
    private final FileSystemService fileSystemService;

    @GetMapping
    public void getDirectory() {
        // path checked through @VALID in record
    }

    @PostMapping
    public void createNewDirectory(@RequestParam @Pattern(regexp = ".*/") String path,
                                   Principal principal) {
        Optional<User> userOpt = userService.getUserByUsername(principal.getName());
        if(userOpt.isPresent()){
            FileNode fileNode = fileSystemService.createDirectory(userOpt.get(), path);
        }
    }
}
