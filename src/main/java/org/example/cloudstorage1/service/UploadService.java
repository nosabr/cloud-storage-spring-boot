package org.example.cloudstorage1.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cloudstorage1.entity.FileNode;
import org.example.cloudstorage1.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class UploadService {
    public List<FileNode> upload(String path, List<MultipartFile> files, User user) {
        for(MultipartFile file : files){
            validatName(path,file,user);
        }

        for(MultipartFile file : files){
            checkConflict(path,file,user);
        }
        for(MultipartFile file : files){
            createFolderIfNotExists(path,file,user);
            createFileNodeOfFile(path,file,user);
        }
        for(MultipartFile file : files){
            uploadToMinio(file,user);
        }
        
        return null;
    }

    private void uploadToMinio(MultipartFile file, User user) {
    }

    private void createFileNodeOfFile(String path, MultipartFile file, User user) {
    }

    private void validatName(String path, MultipartFile file, User user) {
    }

    private void createFolderIfNotExists(String path, MultipartFile file, User user) {
    }

    private void checkConflict(String path, MultipartFile file, User user) {
    }
}
