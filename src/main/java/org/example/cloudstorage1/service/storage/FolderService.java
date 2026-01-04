package org.example.cloudstorage1.service.storage;

import org.example.cloudstorage1.exception.StorageException;

public interface FolderService {
    void createFolder(String name) throws StorageException;
}
