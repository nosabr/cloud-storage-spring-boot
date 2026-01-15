package org.example.cloudstorage1.util;

import lombok.RequiredArgsConstructor;
import org.example.cloudstorage1.entity.FileNode;
import org.example.cloudstorage1.entity.FileType;
import org.example.cloudstorage1.exception.FolderNotFoundException;
import org.example.cloudstorage1.repository.FileMetadataRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PathBuilder {

    private final FileMetadataRepository fileMetadataRepository;

    /**
     * Построить полный путь через рекурсивный обход parentId
     */
    public String buildFullPath(FileNode node) {
        if (node.getParentId() == null) {
            // Корневой элемент
            return node.getType() == FileType.DIRECTORY
                    ? node.getName() + "/"
                    : node.getName();
        }

        // Рекурсивно получить путь родителя
        FileNode parent = fileMetadataRepository.findById(node.getParentId())
                .orElseThrow(() -> new RuntimeException("Parent not found"));

        String parentPath = buildFullPath(parent);

        // Добавить текущий элемент
        return node.getType() == FileType.DIRECTORY
                ? parentPath + node.getName() + "/"
                : parentPath + node.getName();
    }

    /**
     * Построить путь родителя
     */
    public String buildParentPath(FileNode node) {
        if (node.getParentId() == null) {
            return "";
        }

        FileNode parent = fileMetadataRepository.findById(node.getParentId())
                .orElseThrow(() -> new RuntimeException("Parent not found"));

        return buildFullPath(parent);
    }

    /**
     * Найти папку по полному пути (парсинг пути)
     */
    public FileNode findByPath(Long ownerId, String path) {
        if (path == null || path.isEmpty()) {
            return null; // корень
        }

        // Разбить путь: "folder1/folder2/folder3/" -> ["folder1", "folder2", "folder3"]
        String[] parts = path.split("/");

        Long currentParentId = null;
        FileNode current = null;

        for (String part : parts) {
            if (part.isEmpty()) continue;

            // Найти папку по имени и родителю
            current = fileMetadataRepository
                    .findByOwnerIdAndParentIdAndName(ownerId, currentParentId, part)
                    .orElseThrow(() -> new FolderNotFoundException("Folder not found: " + part));

            currentParentId = current.getId();
        }

        return current;
    }
}
