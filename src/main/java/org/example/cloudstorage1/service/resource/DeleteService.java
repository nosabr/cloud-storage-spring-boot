package org.example.cloudstorage1.service.resource;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cloudstorage1.entity.FileNode;
import org.example.cloudstorage1.entity.FileType;
import org.example.cloudstorage1.entity.User;
import org.example.cloudstorage1.repository.FileNodeRepository;
import org.example.cloudstorage1.service.storage.ObjectStorageService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class DeleteService {

    private final FileNodeRepository fileNodeRepository;
    private final ObjectStorageService objectStorageService;

    public void deleteResource(User user, FileNode fileNode) {
        if(fileNode.getType() == FileType.FILE){
            deleteFile(user, fileNode);
        } else {
            deleteDirectory(user, fileNode);
        }
    }

    public void deleteFile(User user, FileNode fileNode){
        String objectName = user.getUsername() + "/" + fileNode.getStorageKey();
        objectStorageService.deleteFile(objectName);
        fileNodeRepository.delete(fileNode);
    }

    public void deleteDirectory(User user, FileNode fileNode){
        deleteDirectoryAndAllChildren(user, fileNode);
    }

    private void deleteDirectoryAndAllChildren(User user, FileNode fileNode) {
        List<FileNode> children = fileNodeRepository.findAllByOwnerIdAndParentId(user.getId(), fileNode.getId());
        for(FileNode child : children){
            if(child.getType() == FileType.FILE){
                deleteFile(user,child);
                fileNodeRepository.delete(child);
            } else {
                deleteDirectoryAndAllChildren(user,child);
            }
        }
        fileNodeRepository.delete(fileNode);
    }
}
