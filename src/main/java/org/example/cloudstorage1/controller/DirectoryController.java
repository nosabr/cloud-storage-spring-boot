package org.example.cloudstorage1.controller;

import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cloudstorage1.dto.ResourceResponse;
import org.example.cloudstorage1.entity.FileNode;
import org.example.cloudstorage1.entity.FileType;
import org.example.cloudstorage1.entity.User;
import org.example.cloudstorage1.exception.UserNotFoundException;
import org.example.cloudstorage1.mapper.FileNodeMapper;
import org.example.cloudstorage1.service.DirectoryService;
import org.example.cloudstorage1.service.auth.UserService;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/api/directory")
@RequiredArgsConstructor
public class DirectoryController {

    private final UserService userService;
    private final DirectoryService directoryService;
    private final FileNodeMapper fileNodeMapper;

    @GetMapping
    public ResponseEntity<List<ResourceResponse>> getDirectory(
            @RequestParam(defaultValue = "") String path, Principal principal
    ) {
        log.info("Get directory " + path + "by user: " + principal.getName() );
        User user = userService.getUserByUsername(principal.getName());
        List<FileNode> fileNodeList = directoryService.getDirectoryContent(user, path);
        return ResponseEntity.ok(fileNodeMapper.toResponseList(fileNodeList));
    }

    @PostMapping
    public ResponseEntity<ResourceResponse> createNewDirectory(
            @RequestParam @Pattern(regexp = ".+/") String path,
            Principal principal) {
        log.info("Create new directory " + path + "by user: " + principal.getName() );
        User user = userService.getUserByUsername(principal.getName());
        FileNode fileNode = directoryService.createDirectory(user, path);
        ResourceResponse response = fileNodeMapper.toResponse(fileNode);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
