package org.example.cloudstorage1.service.storage;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import org.example.cloudstorage1.exception.StorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;


@Service
public class FolderServiceImp implements FolderService{
    private final MinioClient minioClient;

    @Value("${storage.bucket-name}")
    private String bucketName;

    public FolderServiceImp(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

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
}
