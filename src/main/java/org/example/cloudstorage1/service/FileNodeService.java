package org.example.cloudstorage1.service;

import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cloudstorage1.dto.ResourceResponse;
import org.example.cloudstorage1.entity.User;
import org.example.cloudstorage1.repository.FileMetadataRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class FileNodeService {
    private final FileMetadataRepository fileMetadataRepository;

    public ResourceResponse createFolder(User user, String path){
        return null;
    }

    public boolean isPathExists(User user, String parentPath) {
        return true;
    }
}
