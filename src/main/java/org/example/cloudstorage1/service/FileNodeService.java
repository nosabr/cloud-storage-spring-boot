package org.example.cloudstorage1.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cloudstorage1.entity.FileNode;
import org.example.cloudstorage1.entity.User;
import org.example.cloudstorage1.repository.FileMetadataRepository;
import org.example.cloudstorage1.service.auth.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.Optional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class FileNodeService {
    private final FileMetadataRepository fileMetadataRepository;
    private final UserService userService;

    public FileNode save(FileNode fileNode) {
        return fileMetadataRepository.save(fileNode);
    }

    public boolean isPathExists(Long userId, String path) {
        Optional<FileNode> fileNodeOpt = fileMetadataRepository.findByOwnerIdAndPath(userId, path);
        return fileNodeOpt.isPresent();
    }

    public Optional<FileNode> findByOwnerIdAndPath(Long userId, String path) {
        return fileMetadataRepository.findByOwnerIdAndPath(userId, path);
    }
}
