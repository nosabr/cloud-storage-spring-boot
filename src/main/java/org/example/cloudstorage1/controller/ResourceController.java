package org.example.cloudstorage1.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cloudstorage1.dto.DownloadResponse;
import org.example.cloudstorage1.dto.ResourceResponse;
import org.example.cloudstorage1.entity.FileNode;
import org.example.cloudstorage1.entity.User;
import org.example.cloudstorage1.mapper.FileNodeMapper;
import org.example.cloudstorage1.service.FileNodeService;
import org.example.cloudstorage1.service.resource.DeleteService;
import org.example.cloudstorage1.service.resource.DownloadService;
import org.example.cloudstorage1.service.resource.MoveService;
import org.example.cloudstorage1.service.resource.UploadService;
import org.example.cloudstorage1.service.auth.UserService;
import org.example.cloudstorage1.util.FolderPathUtil;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    private final DeleteService deleteService;
    private final DownloadService downloadService;
    private final MoveService moveService;

    @GetMapping
    public ResponseEntity<ResourceResponse> getResourceData(
            @RequestParam String path, Principal principal){
        User user = userService.getUserByUsername(principal.getName());
        FileNode fileNode = fileNodeService.getResource(user, path);
        return ResponseEntity.ok(fileNodeMapper.toResponse(fileNode));
    }

    @DeleteMapping
    public ResponseEntity<?> deleteResource(
            @RequestParam @NotBlank String path, Principal principal
    ){
        User user = userService.getUserByUsername(principal.getName());
        FileNode fileNode = fileNodeService.getResource(user, path);
        deleteService.deleteResource(user, fileNode);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<List<ResourceResponse>> uploadResource(
            @RequestParam (defaultValue = "") String path, @RequestParam("object") List<MultipartFile> object,
            Principal principal
    ) throws IOException {
        User user = userService.getUserByUsername(principal.getName());
        if (!path.isEmpty()) {
            fileNodeService.getResource(user, path);
        }
        log.info("uploading file to path {} file {}", user.getUsername(), path, object);
        List<FileNode> fileNodes = uploadService.upload(path, object, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(fileNodeMapper.toResponseList(fileNodes));
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadResource(
            @RequestParam @NotBlank String path, Principal principal
    ){
        User user = userService.getUserByUsername(principal.getName());
        FileNode fileNode = fileNodeService.getResource(user, path);
        DownloadResponse downloadResponse= downloadService.download(user, fileNode);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + downloadResponse.name() + "\"")
                .body(downloadResponse.resource());
    }

    @GetMapping("/move")
    public ResponseEntity<ResourceResponse> moveResource(
            @RequestParam @NotBlank String from,
            @RequestParam @NotBlank String to,
            Principal principal
    ){
        User user = userService.getUserByUsername(principal.getName());
        FileNode fileNode =  fileNodeService.getResource(user, from);
        FileNode movedfileNode = moveService.moveResource(user, fileNode, to);
        return ResponseEntity.ok(fileNodeMapper.toResponse(movedfileNode));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ResourceResponse>> findResource(
            @RequestParam @NotBlank String query,
            Principal principal
    ){
        User user = userService.getUserByUsername(principal.getName());
        List<FileNode> fileNodes = fileNodeService.findByName(user, query);
        return  ResponseEntity.ok(fileNodeMapper.toResponseList(fileNodes));
    }


}
