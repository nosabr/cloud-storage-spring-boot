package org.example.cloudstorage1.service.resource;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cloudstorage1.dto.DownloadResponse;
import org.example.cloudstorage1.entity.FileNode;
import org.example.cloudstorage1.entity.FileType;
import org.example.cloudstorage1.entity.User;
import org.example.cloudstorage1.exception.StorageException;
import org.example.cloudstorage1.repository.FileNodeRepository;
import org.example.cloudstorage1.service.storage.ObjectStorageService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class DownloadService {
    private final ObjectStorageService objectStorageService;
    private final FileNodeRepository fileNodeRepository;

    @Transactional(readOnly = true)
    public DownloadResponse download(User user, FileNode fileNode) {
        if (fileNode.getType() == FileType.FILE) {
            return downloadFile(user, fileNode);
        }
        return downloadFolder(user, fileNode);
    }

    private DownloadResponse downloadFolder(User user, FileNode fileNode) {
        try {
            File tempZipFile = File.createTempFile("folder-", ".zip");
            tempZipFile.deleteOnExit();

            try (FileOutputStream fos = new FileOutputStream(tempZipFile);
                 ZipOutputStream zos = new ZipOutputStream(fos)) {
                addFolderToZip(fileNode, "", zos, user);
            }

            FileSystemResource resource = new FileSystemResource(tempZipFile);
            return new DownloadResponse(fileNode.getName() + ".zip", resource);

        } catch (IOException e) {
            log.error("Failed to create ZIP archive for folder: {}", fileNode.getName(), e);
            throw new StorageException("Failed to create archive for folder: " + fileNode.getName(), e);
        }
    }

    private DownloadResponse downloadFile(User user, FileNode fileNode) {
        try {
            String objectName = user.getUsername() + "/" + fileNode.getStorageKey();
            InputStream inputStream = objectStorageService.downloadFile(objectName);
            return new DownloadResponse(fileNode.getName(), new InputStreamResource(inputStream));
        } catch (Exception e) {
            log.error("Failed to download file: {}", fileNode.getName(), e);
            throw new StorageException("Failed to download file: " + fileNode.getName(), e);
        }
    }

    private void addFolderToZip(FileNode fileNode, String parentPath, ZipOutputStream zos, User user)
            throws IOException {

        List<FileNode> children = fileNodeRepository.findAllByOwnerIdAndParentId(
                user.getId(), fileNode.getId());

        for (FileNode child : children) {
            String currentPath = parentPath.isEmpty()
                    ? child.getName()
                    : parentPath + "/" + child.getName();

            if (child.isFile()) {
                addFileToZip(child, currentPath, zos, user);
            } else {
                ZipEntry dirEntry = new ZipEntry(currentPath + "/");
                zos.putNextEntry(dirEntry);
                zos.closeEntry();
                addFolderToZip(child, currentPath, zos, user);
            }
        }
    }

    private void addFileToZip(FileNode fileNode, String path, ZipOutputStream zos, User user)
            throws IOException {
        try {
            ZipEntry zipEntry = new ZipEntry(path);
            zos.putNextEntry(zipEntry);

            String objectName = user.getUsername() + "/" + fileNode.getStorageKey();

            try (InputStream fileStream = objectStorageService.downloadFile(objectName)) {
                byte[] buffer = new byte[8192];
                int length;
                while ((length = fileStream.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }
            }

            zos.closeEntry();

        } catch (IOException e) {
            log.error("Failed to add file '{}' to ZIP archive", fileNode.getName(), e);
            throw new StorageException("Failed to add file to archive: " + fileNode.getName(), e);
        }
    }
}