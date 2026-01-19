package org.example.cloudstorage1.service.resource;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cloudstorage1.dto.ParsedFile;
import org.example.cloudstorage1.entity.FileNode;
import org.example.cloudstorage1.entity.FileType;
import org.example.cloudstorage1.entity.User;
import org.example.cloudstorage1.exception.ResourceConflictException;
import org.example.cloudstorage1.exception.ResourceNotFoundException;
import org.example.cloudstorage1.exception.StorageException;
import org.example.cloudstorage1.repository.FileNodeRepository;
import org.example.cloudstorage1.service.DirectoryService;
import org.example.cloudstorage1.service.storage.ObjectStorageService;
import org.example.cloudstorage1.util.ResourceUtil;
import org.example.cloudstorage1.util.ResourceValidationUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class UploadService {
    private final FileNodeRepository fileNodeRepository;
    private final DirectoryService directoryService;
    private final ObjectStorageService objectStorageService;

    public List<FileNode> upload(FileNode parentFileNode, List<MultipartFile> files, User user) throws IOException {
        log.info("Uploading files to directory: {}", files);
        checkForConflict(parentFileNode, files, user);
        List<ParsedFile> parsedFiles = parseFiles(files);
        createFolderIfNotExist(parentFileNode, parsedFiles, user);
        validateFileNames(parsedFiles);
        try {
            uploadFilesToStorage(user,parsedFiles);
        } catch (Exception e) {
            log.error("Failed to upload files to storage", e);
            throw new StorageException(e.getMessage());
        }
        try {
            return addParsedFilesToDB(parentFileNode, parsedFiles, user);
        } catch (Exception e) {
            log.error("Failed to upload files to Database", e);
            throw new StorageException(e.getMessage());
        }
    }

    private void checkForConflict(FileNode parentFileNode, List<MultipartFile> files, User user) {
        for (MultipartFile file : files) {
            String fullPath = parentFileNode.getPath() + file.getOriginalFilename();
            fileNodeRepository.findByOwnerIdAndPath(user.getId(), fullPath)
                    .ifPresent(existingFile -> {
                        throw new ResourceConflictException("Resource conflict" + fullPath);
                    });
        }
    }

    private List<ParsedFile> parseFiles(List<MultipartFile> files) {
        List<ParsedFile> parsedFiles = new ArrayList<>();
        for(MultipartFile file : files) {
            List<String> segments = ResourceUtil.parsePathIntoSegments(file.getOriginalFilename());
            StringBuilder path = new StringBuilder();
            for(int i = 0; i < segments.size() - 1; i++) {
                path.append(segments.get(i)).append('/'); // создаем путь до файла
            }
            ParsedFile parsedFile = new ParsedFile(file, path.toString(), segments, UUID.randomUUID().toString());
            parsedFiles.add(parsedFile);
        }
        return parsedFiles;
    }

    private void createFolderIfNotExist(FileNode parentFileNode, List<ParsedFile> parsedFiles, User user) {
        for (ParsedFile parsedFile : parsedFiles) {
            List<String> segments = parsedFile.segments();
            String currentPath = parentFileNode.getPath();
            for(int i = 0; i < segments.size() - 1; i++) {
                currentPath += segments.get(i) + '/';
                try {
                    directoryService.createDirectory(user, currentPath);
                } catch (ResourceConflictException ignored) {}
            }
        }
    }

    private void validateFileNames(List<ParsedFile> parsedFiles) {
        for (ParsedFile parsedFile : parsedFiles) {
            ResourceValidationUtil.validateFileName(parsedFile.getFileName());
        }
    }

    private void uploadFilesToStorage(User user, List<ParsedFile> files) {
        for (ParsedFile file : files) {
            String fileName = user.getUsername() + "/" + file.uuid();
            try {
                objectStorageService.uploadFile(fileName, file.file().getInputStream(), file.file().getSize());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private List<FileNode> addParsedFilesToDB(FileNode parentFileNode, List<ParsedFile> parsedFiles, User user) {
        List<FileNode> addedFileNodes = new ArrayList<>();
        for (ParsedFile parsedFile : parsedFiles) {
            FileNode fileNode = createFileNode(parentFileNode, parsedFile, user);
            addedFileNodes.add(fileNodeRepository.save(fileNode));
        }
        return addedFileNodes;
    }

    private FileNode createFileNode(FileNode parentFileNode, ParsedFile parsedFile, User user) {
        Long lastParentId = getFileParentId(parentFileNode, parsedFile, user);
        return FileNode.builder()
                .name(parsedFile.getFileName())
                .type(FileType.FILE)
                .parentId(lastParentId)
                .path(parentFileNode.getPath() + parsedFile.path())
                .ownerId(user.getId())
                .storageKey(parsedFile.uuid())
                .size(parsedFile.file().getSize())
                .build();
    }

    private Long getFileParentId(FileNode parentFileNode, ParsedFile parsedFile, User user) {
        if(parsedFile.path().isEmpty()){
            return parentFileNode.getId();
        } else {
            String path = parentFileNode.getPath() + parsedFile.path();
            FileNode newParentFileNode = fileNodeRepository.findByOwnerIdAndPath(user.getId(), path)
                    .orElseThrow(() -> new ResourceNotFoundException("Resource not found " + path));
            return newParentFileNode.getId();
        }
    }
}
