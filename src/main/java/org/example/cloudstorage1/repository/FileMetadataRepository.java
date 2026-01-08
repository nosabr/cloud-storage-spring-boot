package org.example.cloudstorage1.repository;

import org.example.cloudstorage1.entity.FileNode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileMetadataRepository extends JpaRepository<FileNode, Long> {
}
