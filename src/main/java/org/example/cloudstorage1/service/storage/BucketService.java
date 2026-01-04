package org.example.cloudstorage1.service.storage;

import org.example.cloudstorage1.exception.StorageException;

public interface BucketService {

    boolean bucketExists(String bucketName) throws StorageException;

    void createBucket(String bucketName) throws StorageException;

    void createBucketIfNotExists(String bucketName) throws StorageException;
}
