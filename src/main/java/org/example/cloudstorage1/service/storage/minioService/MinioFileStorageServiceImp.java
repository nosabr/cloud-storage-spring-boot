package org.example.cloudstorage1.service.storage.minioService;

import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.example.cloudstorage1.exception.StorageException;
import org.example.cloudstorage1.service.storage.FileStorageService;
import org.springframework.beans.factory.annotation.Value;

import java.io.InputStream;

@RequiredArgsConstructor
public class MinioFileStorageServiceImp implements FileStorageService {

    private final MinioClient minioClient;
    @Value("${storage.bucket-name}")
    private String bucketName;


    @Override
    public InputStream downloadFile(String objectName) throws StorageException {
        return null;
    }

    @Override
    public void uploadFile(String objectName, InputStream inputStream) throws StorageException {

    }

    @Override
    public void deleteFile(String objectName) throws StorageException {

    }

    @Override
    public void moveFile(String srcObjectName, String destObjectName) throws StorageException {

    }

    @Override
    public void renameFile(String srcObjectName, String newObjectName) throws StorageException {

    }

    @Override
    public boolean exists(String objectName) throws StorageException {
        return false;
    }
}
