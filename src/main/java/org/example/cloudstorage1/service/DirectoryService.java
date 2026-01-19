package org.example.cloudstorage1.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cloudstorage1.entity.FileNode;
import org.example.cloudstorage1.entity.FileType;
import org.example.cloudstorage1.entity.User;
import org.example.cloudstorage1.exception.ResourceConflictException;
import org.example.cloudstorage1.exception.ResourceNotFoundException;
import org.example.cloudstorage1.repository.FileNodeRepository;
import org.example.cloudstorage1.util.FolderValidationUtil;
import org.example.cloudstorage1.util.FolderPathUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DirectoryService {

    private final FileNodeRepository fileNodeRepository;

    public FileNode createDirectory(User user, String fullPath) {
        String parentPath = FolderPathUtil.getParentPath(fullPath);
        String folderName = FolderPathUtil.getFolderName(fullPath);
        FolderValidationUtil.validateFolderName(folderName);
        if (fileNodeRepository.findByOwnerIdAndPath(user.getId(), fullPath).isPresent()) {
            log.warn("Folder already exists for user: {}", fullPath);
            throw new ResourceConflictException("The path already exists!");
        }
        Long parentId = getParentId(user.getId(), parentPath);

        FileNode fileNode = FileNode.builder()
                .name(folderName)
                .type(FileType.DIRECTORY)
                .parentId(parentId)
                .path(fullPath)
                .ownerId(user.getId())
                .build();
        log.info("Created repository" + fileNode);
        return fileNodeRepository.save(fileNode);
    }

    public List<FileNode> getDirectoryContent(User user, String fullPath) {
        if(fullPath == null || fullPath.isEmpty()){
            return fileNodeRepository.findAllByOwnerIdAndParentId(user.getId(), null);
        }
        FileNode folder = fileNodeRepository.findByOwnerIdAndPath(user.getId(), fullPath)
                .orElseThrow(() -> new ResourceNotFoundException("Folder not found" + fullPath));
        return fileNodeRepository.findAllByOwnerIdAndParentId(user.getId(), folder.getId());
    }

    public Long getParentId(Long userId, String parentPath) {
        if (parentPath.isEmpty()) {
            return null;
        }
        return fileNodeRepository.findByOwnerIdAndPath(userId, parentPath)
                .map(FileNode :: getId)
                .orElseThrow(() -> new ResourceNotFoundException("Folder not found " + parentPath));

    }
}
