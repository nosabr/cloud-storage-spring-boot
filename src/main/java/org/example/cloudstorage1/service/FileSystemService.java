package org.example.cloudstorage1.service;

import lombok.RequiredArgsConstructor;
import org.example.cloudstorage1.entity.FileNode;
import org.example.cloudstorage1.entity.FileType;
import org.example.cloudstorage1.entity.User;
import org.example.cloudstorage1.exception.FolderConflictException;
import org.example.cloudstorage1.exception.FolderNotFoundException;
import org.example.cloudstorage1.repository.FileMetadataRepository;
import org.example.cloudstorage1.util.FolderNameValidationUtil;
import org.example.cloudstorage1.util.FolderUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FileSystemService {

    private final FileNodeService fileNodeService;
    private final FileMetadataRepository  fileMetadataRepository;

    public FileNode createDirectory(User user, String fullPath) {

        String parentPath = FolderUtil.getParentPath(fullPath);
        String folderName = FolderUtil.getFolderName(fullPath);

        FolderNameValidationUtil.validateFolderName(folderName);

        if (fileNodeService.isPathExists(user.getId(), fullPath)) {
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

        return fileMetadataRepository.save(fileNode);
    }

    private Long getParentId(Long userId, String parentPath) {
        if (parentPath.isEmpty()) {
            return null;
        }
        Optional<FileNode> fileNodeOptional = fileMetadataRepository
                .findByOwnerIdAndPath(userId, parentPath);
        if(fileNodeOptional.isPresent()) {
            return fileNodeOptional.get().getId();
        }
        throw new FolderNotFoundException("The path does not exist!");
    }

    public List<FileNode> getDirectoryContent(User user, String fullPath) {
        return null;
    }

    public void downloadFile() {
    }

    public void uploadFile() {
    }

    public void deleteFile() {
    }


}
