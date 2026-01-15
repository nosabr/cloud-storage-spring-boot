package org.example.cloudstorage1.repository;

import org.example.cloudstorage1.entity.FileNode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.File;
import java.util.List;
import java.util.Optional;

public interface FileMetadataRepository extends JpaRepository<FileNode, Long> {
    Optional<FileNode> findByOwnerIdAndPath(Long userId, String path);
    List<FileNode> findAllByOwnerIdAndParentId(Long userId, Long parentId);
    Optional<FileNode> findByOwnerIdAndParentIdAndName(Long ownerId, Long currentParentId, String name);
}
