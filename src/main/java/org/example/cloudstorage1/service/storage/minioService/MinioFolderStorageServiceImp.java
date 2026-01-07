package org.example.cloudstorage1.service.storage.minioService;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.example.cloudstorage1.exception.StorageException;
import org.example.cloudstorage1.service.storage.FolderStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.List;


@Service
@RequiredArgsConstructor
public class MinioFolderStorageServiceImp implements FolderStorageService {
    private final MinioClient minioClient;

    @Value("${storage.bucket-name}")
    private String bucketName;

    @Override
    public void createFolder(String name) throws StorageException {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder().bucket(bucketName).object(name + "/").stream(
                                    new ByteArrayInputStream(new byte[] {}), 0, -1)
                            .build());
        } catch (Exception e) {
            throw new StorageException("Error while creating the folder", e);
        }
    }

    @Override
    public List<String> getFolderContent(String path) throws StorageException {
        return List.of();
    }

    @Override
    public void renameFolder(String oldName, String newName) throws StorageException {

    }
}
