package org.example.cloudstorage1.service;

import lombok.RequiredArgsConstructor;
import org.example.cloudstorage1.dto.ResourceResponse;
import org.example.cloudstorage1.entity.User;
import org.example.cloudstorage1.exception.FolderConflictException;
import org.example.cloudstorage1.exception.FolderNotFoundException;
import org.example.cloudstorage1.repository.FileMetadataRepository;
import org.example.cloudstorage1.service.auth.UserService;
import org.example.cloudstorage1.service.storage.ObjectStorageService;
import org.example.cloudstorage1.util.PathValidator;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileSystemService {

    private final ObjectStorageService objectStorageService;
    private final FileNodeService fileNodeService;


    public ResourceResponse createDirectory(User user, String fullPath){

        String parentPath = fullPath.substring(0, fullPath.lastIndexOf("/"));
        String childPath = fullPath.substring(fullPath.lastIndexOf("/"));

        if(!PathValidator.isPathValid(fullPath)){ // checking that path is valid hz nuzhno li
            throw new BadCredentialsException("Invalid path");
        }

        if(!fileNodeService.isPathExists(user, parentPath)){
            throw new FolderNotFoundException("Parent folder not exists");
        }

        if(fileNodeService.isPathExists(user, fullPath)){ // check that directory already exists
            throw new FolderConflictException("The path already exists!");
        }

        return fileNodeService.createFolder(user, fullPath);// типа разделить на папки
    }

}
