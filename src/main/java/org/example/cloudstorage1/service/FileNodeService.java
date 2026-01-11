package org.example.cloudstorage1.service;

import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cloudstorage1.dto.ResourceResponse;
import org.example.cloudstorage1.entity.FileNode;
import org.example.cloudstorage1.entity.FileType;
import org.example.cloudstorage1.entity.User;
import org.example.cloudstorage1.exception.FolderConflictException;
import org.example.cloudstorage1.exception.FolderNotFoundException;
import org.example.cloudstorage1.repository.FileMetadataRepository;
import org.example.cloudstorage1.service.auth.UserService;
import org.example.cloudstorage1.util.PathValidator;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class FileNodeService {
    private final FileMetadataRepository fileMetadataRepository;
    private final UserService userService;

    public FileNode save(FileNode fileNode){
        return fileMetadataRepository.save(fileNode);
    }

    public boolean isPathExists(User user, String parentPath) {
        return true;
    }
}
