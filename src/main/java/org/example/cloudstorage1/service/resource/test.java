package org.example.cloudstorage1.service.resource;

import io.minio.GetObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cloudstorage1.dto.DownloadResponse;
import org.example.cloudstorage1.entity.FileNode;
import org.example.cloudstorage1.entity.FileType;
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
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class test {

    private final FileNodeRepository fileNodeRepository;
    private final ObjectStorageService minioClient;

    private String bucketName = "Sabr";

    @Transactional(readOnly = true)
    public DownloadResponse downloadResource(String path, Long userId) {
        // 1. Валидация пути
        validatePath(path);

        // 2. Найти FileNode по пути и userId
        FileNode fileNode = findFileNodeByPath(path, userId);

        // 3. Проверить ownership
        if (!fileNode.getOwnerId().equals(userId)) {
            throw new StorageException("Access denied to resource");
        }

        // 4. Определить тип и вернуть соответствующий результат
        if (fileNode.getType() == FileType.FILE) {
            return downloadFile(fileNode);
        } else {
            return downloadFolder(fileNode);
        }
    }

    private DownloadResponse downloadFile(FileNode fileNode) {
        try {
            InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileNode.getStoragePath())
                            .build()
            );

            InputStreamResource resource = new InputStreamResource(stream);

            return DownloadResult.builder()
                    .filename(fileNode.getName())
                    .resource(resource)
                    .build();

        } catch (Exception e) {
            log.error("Failed to download file from MinIO: {}", fileNode.getStoragePath(), e);
            throw new StorageException("Failed to download file", e);
        }
    }

    private DownloadResponse downloadFolder(FileNode folder) {
        try {
            // Создаём временный файл для ZIP
            File tempZipFile = File.createTempFile("folder-", ".zip");
            tempZipFile.deleteOnExit(); // Удалится при завершении JVM

            try (FileOutputStream fos = new FileOutputStream(tempZipFile);
                 ZipOutputStream zos = new ZipOutputStream(fos)) {

                // Рекурсивно добавляем файлы в архив
                addFolderToZip(folder, "", zos);
            }

            // Читаем ZIP как Resource
            FileSystemResource resource = new FileSystemResource(tempZipFile);

            return DownloadResponse.builder()
                    .filename(folder.getName() + ".zip")
                    .resource(resource)
                    .build();

        } catch (IOException e) {
            log.error("Failed to create ZIP archive for folder: {}", folder.getName(), e);
            throw new StorageException("Failed to create archive", e);
        }
    }

    private void addFolderToZip(FileNode folder, String parentPath, ZipOutputStream zos) throws IOException {
        // Получаем всех детей текущей папки
        List<FileNode> children = fileNodeRepository.findByParentAndUserId(folder, folder.getUser().getId());

        for (FileNode child : children) {
            String currentPath = parentPath.isEmpty()
                    ? child.getName()
                    : parentPath + "/" + child.getName();

            if (child.isFile()) {
                // Добавляем файл в ZIP
                addFileToZip(child, currentPath, zos);
            } else {
                // Создаём директорию в ZIP
                ZipEntry dirEntry = new ZipEntry(currentPath + "/");
                zos.putNextEntry(dirEntry);
                zos.closeEntry();

                // Рекурсивно обрабатываем поддиректории
                addFolderToZip(child, currentPath, zos);
            }
        }
    }

    private void addFileToZip(FileNode file, String path, ZipOutputStream zos) throws IOException {
        try {
            ZipEntry zipEntry = new ZipEntry(path);
            zos.putNextEntry(zipEntry);

            // Стримим файл из MinIO напрямую в ZIP
            try (InputStream fileStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(file.getStoragePath())
                            .build())) {

                byte[] buffer = new byte[8192];
                int length;
                while ((length = fileStream.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }
            }

            zos.closeEntry();

        } catch (Exception e) {
            log.error("Failed to add file to ZIP: {}", file.getName(), e);
            throw new IOException("Failed to add file to archive", e);
        }
    }

    private FileNode findFileNodeByPath(String path, Long userId) {
        // Разбиваем путь на части
        String[] parts = path.split("/");

        FileNode current = null;

        for (String part : parts) {
            if (part.isEmpty()) continue;

            current = fileNodeRepository
                    .findByNameAndParentAndUserId(part, current, userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Resource not found: " + path));
        }

        if (current == null) {
            throw new ResourceNotFoundException("Resource not found: " + path);
        }

        return current;
    }

    private void validatePath(String path) {
        if (path.contains("..") || path.startsWith("/")) {
            throw new InvalidPathException("Invalid path format");
        }
    }
}
