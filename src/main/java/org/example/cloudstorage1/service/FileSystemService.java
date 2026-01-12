package org.example.cloudstorage1.service;

import lombok.RequiredArgsConstructor;
import org.example.cloudstorage1.dto.ResourceResponse;
import org.example.cloudstorage1.entity.FileNode;
import org.example.cloudstorage1.entity.FileType;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FileSystemService {

    private final FileNodeService fileNodeService;

    public FileNode createDirectory(User user, String fullPath){

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

        return null;
    }

    public List<FileNode> getDirectoryContent(User user, String fullPath){
        return null;
    }

    public void downloadFile(){}

    public void uploadFile(){}

    public void deleteFile(){}


    public void createUserBaseFolder(User user){
        FileNode fileNode = new FileNode(user.getUsername(), FileType.DIRECTORY, null, user.getId());
        fileNodeService.save(fileNode);
    }

}
