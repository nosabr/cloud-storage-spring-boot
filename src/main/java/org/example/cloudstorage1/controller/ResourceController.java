package org.example.cloudstorage1.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cloudstorage1.dto.ResourceResponse;
import org.example.cloudstorage1.entity.FileNode;
import org.example.cloudstorage1.entity.User;
import org.example.cloudstorage1.mapper.FileNodeMapper;
import org.example.cloudstorage1.service.FileNodeService;
import org.example.cloudstorage1.service.UploadService;
import org.example.cloudstorage1.service.auth.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/resource")
@RequiredArgsConstructor
public class ResourceController {
    private final FileNodeService fileNodeService;
    private final UserService userService;
    private final FileNodeMapper fileNodeMapper;
    private final UploadService uploadService;

    @GetMapping
    public ResponseEntity<ResourceResponse> getResourceData(
            @RequestParam String path, Principal principal){
        User user = userService.getUserByUsername(principal.getName());
        FileNode fileNode = fileNodeService.getResource(user, path);
        return ResponseEntity.ok(fileNodeMapper.toResponse(fileNode));
    }

    @DeleteMapping
    public void deleteResource(){}

    @PostMapping
    public ResponseEntity<List<ResourceResponse>> uploadResource(
            @RequestParam String path, List<MultipartFile> files,
            Principal principal
    ){
        User user = userService.getUserByUsername(principal.getName());
        List<FileNode> fileNodes = uploadService.upload(path, files, user);
        return ResponseEntity.ok(fileNodeMapper.toResponseList(fileNodes));
    }

    @GetMapping("/download")
    public void downloadResource(){}

    @GetMapping("/move")
    public void moveResource(){}

    @GetMapping("/search")
    public void findResource(){}


}
