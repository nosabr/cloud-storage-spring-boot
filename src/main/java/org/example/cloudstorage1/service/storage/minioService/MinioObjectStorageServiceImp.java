package org.example.cloudstorage1.service.storage.minioService;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import org.example.cloudstorage1.exception.StorageException;
import org.example.cloudstorage1.service.storage.ObjectStorageService;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RequiredArgsConstructor
public class MinioObjectStorageServiceImp implements ObjectStorageService {

    private final MinioClient minioClient;
    @Value("${storage.bucket-name}")
    private String bucketName;


    @Override
    public InputStream downloadFile(String objectName) throws StorageException {
        try (InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket("my-bucketname")
                        .object("my-objectname")
                        .build())) {
            return stream;
        } catch (Exception e){
            throw new StorageException("Storage exception ", e);
        }
    }

    @Override
    public void uploadFile(String objectName, InputStream inputStream, Long size) throws StorageException {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder().bucket(bucketName).object(objectName).stream(
                                    inputStream, size, -1)
                            .contentType("application/octet-stream")
                            .build());
        } catch (Exception e) {
            throw new StorageException("Storage exception ", e);
        }
    }

    @Override
    public void deleteFile(String objectName) throws StorageException {
        try{
            minioClient.removeObject(
                    RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build());
        } catch (Exception e){
            throw new StorageException("Storage exception ", e);
        }
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
