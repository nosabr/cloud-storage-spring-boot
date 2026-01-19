package org.example.cloudstorage1.service.resource;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cloudstorage1.dto.DownloadResponse;
import org.example.cloudstorage1.entity.FileNode;
import org.example.cloudstorage1.entity.FileType;
import org.example.cloudstorage1.entity.User;
import org.example.cloudstorage1.repository.FileNodeRepository;
import org.example.cloudstorage1.service.storage.ObjectStorageService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class DownloadService {
    private final ObjectStorageService objectStorageService;
    private final FileNodeRepository fileNodeRepository;

    public DownloadResponse download(User user, FileNode fileNode) {
        if(fileNode.getType() == FileType.FILE){
            return downloadFile(user,fileNode);
        }
        return downloadFolder(user, fileNode);
    }

    private DownloadResponse downloadFolder(User user, FileNode fileNode) {
        try {
            File tempZipFile = File.createTempFile("folder-", ".zip");
            tempZipFile.deleteOnExit();
            try (FileOutputStream fos = new FileOutputStream(tempZipFile);
                 ZipOutputStream zos = new ZipOutputStream(fos)){
                addFolderToZip(fileNode, " ", zos);
            }

            FileSystemResource resource = new FileSystemResource(tempZipFile);
            return new DownloadResponse(fileNode.getName() + ".zip", resource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private DownloadResponse downloadFile(User user, FileNode fileNode) {
        String objectName = user.getUsername() + "/" + fileNode.getStorageKey();
        InputStream inputStream = objectStorageService.downloadFile(objectName);
        return new DownloadResponse(fileNode.getName(), new InputStreamResource(inputStream));
    }

    private void addFolderToZip(FileNode fileNode, String parentPath, ZipOutputStream zos) {
        List<FileNode> children = fileNodeRepository.findAllByOwnerIdAndParentId(
                fileNode.getOwnerId(), fileNode.getId());

        for(FileNode child : children){
            String currentPath = parentPath.isEmpty()
                    ? child.getName() : parentPath + child.getName();
            if(fileNode.isFile()){
                addFileToZip(child, currentPath, zos);
            }
        }
    }

    private void addFileToZip(FileNode fileNode, String path, ZipOutputStream zos) {
        try{
            ZipEntry zipEntry = new ZipEntry(path);
            zos.putNextEntry(zipEntry);
            String objectName =
            try(InputStream fileStream = objectStorageService.downloadFile(fileNod))
        } catch (Exception e){
            throw new RuntimeException();
        }
    }
}
