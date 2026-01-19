package org.example.cloudstorage1.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cloudstorage1.entity.FileNode;
import org.example.cloudstorage1.entity.User;
import org.example.cloudstorage1.exception.ResourceNotFoundException;
import org.example.cloudstorage1.repository.FileNodeRepository;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileNodeService {
    private final FileNodeRepository fileNodeRepository;

    public FileNode getResource(User user, String fullPath){
        return fileNodeRepository.findByOwnerIdAndPath(user.getId(), fullPath)
                .orElseThrow(() -> new ResourceNotFoundException("Ресурс не найден " + fullPath));
    }


}
