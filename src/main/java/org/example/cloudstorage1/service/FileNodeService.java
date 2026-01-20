package org.example.cloudstorage1.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cloudstorage1.entity.FileNode;
import org.example.cloudstorage1.entity.User;
import org.example.cloudstorage1.exception.ResourceNotFoundException;
import org.example.cloudstorage1.repository.FileNodeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileNodeService {
    private final FileNodeRepository fileNodeRepository;

    public FileNode getResource(User user, String fullPath){
        return fileNodeRepository.findByOwnerIdAndPath(user.getId(), fullPath)
                .orElseThrow(() -> new ResourceNotFoundException("Ресурс не найден " + fullPath));
    }

    public List<FileNode> findByName(User user, String name){
        return fileNodeRepository.findByOwnerIdAndName(user.getId(), name);
    }

    public void updateAllChildren(FileNode fileNode) {
        if(fileNode.isFile()){
            return;
        }
        List<FileNode> children = fileNodeRepository.findAllByOwnerIdAndParentId(
                fileNode.getOwnerId(), fileNode.getId());
        String path = fileNode.getPath();
        for(FileNode child : children){
            String newPath = path + child.getName() + (child.isFile() ? "" : "/");
            child.setPath(newPath);
            fileNodeRepository.save(child);
            updateAllChildren(child);
        }
    }
}
