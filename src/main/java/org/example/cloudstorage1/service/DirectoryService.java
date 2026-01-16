package org.example.cloudstorage1.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cloudstorage1.entity.FileNode;
import org.example.cloudstorage1.entity.FileType;
import org.example.cloudstorage1.entity.User;
import org.example.cloudstorage1.exception.FolderConflictException;
import org.example.cloudstorage1.exception.ResourceNotFoundException;
import org.example.cloudstorage1.repository.FileMetadataRepository;
import org.example.cloudstorage1.util.FolderNameValidationUtil;
import org.example.cloudstorage1.util.FolderPathUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DirectoryService {

    private final FileMetadataRepository fileMetadataRepository;

    public FileNode createDirectory(User user, String fullPath) {
        String parentPath = FolderPathUtil.getParentPath(fullPath);
        String folderName = FolderPathUtil.getFolderName(fullPath);
        FolderNameValidationUtil.validateFolderName(folderName);
        if (fileMetadataRepository.findByOwnerIdAndPath(user.getId(), fullPath).isPresent()) {
            log.warn("Folder already exists for user: {}", fullPath);
            throw new FolderConflictException("The path already exists!");
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
        return fileMetadataRepository.save(fileNode);
    }

    public List<FileNode> getDirectoryContent(User user, String fullPath) {
        if(fullPath == null || fullPath.isEmpty()){
            return fileMetadataRepository.findAllByOwnerIdAndParentId(user.getId(), null);
        }
        FileNode folder = fileMetadataRepository.findByOwnerIdAndPath(user.getId(), fullPath)
                .orElseThrow(() -> new ResourceNotFoundException("Folder not found" + fullPath));
        return fileMetadataRepository.findAllByOwnerIdAndParentId(user.getId(), folder.getId());
    }

    private Long getParentId(Long userId, String parentPath) {
        if (parentPath.isEmpty()) {
            return null;
        }
        return fileMetadataRepository.findByOwnerIdAndPath(userId, parentPath)
                .map(FileNode :: getId)
                .orElseThrow(() -> new ResourceNotFoundException("Folder not found " + parentPath));

    }
}
