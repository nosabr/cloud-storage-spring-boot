package org.example.cloudstorage1.service.storage;

import org.example.cloudstorage1.exception.StorageException;

import java.util.List;

public interface FolderStorageService {
    void createFolder(String name) throws StorageException;
    void renameFolder(String oldName, String newName) throws StorageException;
    List<String> getFolderContent(String path) throws StorageException;
}
