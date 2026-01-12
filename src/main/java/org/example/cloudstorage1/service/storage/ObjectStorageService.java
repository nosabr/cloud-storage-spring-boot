package org.example.cloudstorage1.service.storage;

import org.example.cloudstorage1.exception.StorageException;
import org.springframework.stereotype.Service;

import java.io.InputStream;


@Service
public interface ObjectStorageService {
    InputStream downloadFile(String objectName) throws StorageException;
    void uploadFile(String objectName, InputStream inputStream) throws StorageException;
    void deleteFile(String objectName) throws StorageException;
    void moveFile(String srcObjectName, String destObjectName) throws StorageException;
    void renameFile(String srcObjectName, String newObjectName) throws StorageException;
    boolean exists(String objectName) throws StorageException;
}
