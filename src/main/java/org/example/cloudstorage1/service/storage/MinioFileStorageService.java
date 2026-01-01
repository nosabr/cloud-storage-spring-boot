package org.example.cloudstorage1.service.storage;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.example.cloudstorage1.exception.StorageException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MinioFileStorageService implements FileStorageService{

    private final MinioClient minioClient;

    public MinioFileStorageService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @Override
    public void createBucket(String bucketName) throws StorageException {
        log.info("Creating bucket {}", bucketName);
        try {
            minioClient.makeBucket(
                    MakeBucketArgs.builder()
                            .bucket(bucketName)
                            .build()
            );
            log.info("Bucket created successfully");
        } catch (Exception e) {
            log.error("Error creating bucket", e);
            throw new StorageException("Error while creating bucket", e);
        }
    }

    @Override
    public boolean bucketExists(String bucketName) throws StorageException {
        try {
            return minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(bucketName)
                            .build()
            );
        } catch (Exception e) {
            log.error("Error while checking if bucket exists", e);
            throw new StorageException("Error while checking if bucket exists", e);
        }
    }

    @Override
    public void createBucketIfNotExists(String bucketName) throws StorageException {
        log.info("Checking is bucket exists '{}'...", bucketName);
        if(!bucketExists(bucketName)){
            log.info("Creating bucket '{}'...", bucketName);
            createBucket(bucketName);
        } else {
            log.info("Bucket '{}' already exists.", bucketName);
        }
    }
}
