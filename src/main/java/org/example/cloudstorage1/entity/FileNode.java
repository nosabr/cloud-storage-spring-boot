package org.example.cloudstorage1.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "file_node", indexes = {
        @Index(name = "idx_parent_id", columnList = "parent_id"),
        @Index(name = "idx_owner_id", columnList = "owner_id"),
        @Index(name = "idx_owner_parent", columnList = "owner_id, parent_id") // составной
})
public class FileNode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FileType type;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(nullable = false)
    private String path;

    @Column(name = "owner_id",nullable = false)
    private Long ownerId;

    @Column(name = "storage_key")
    private String storageKey;

    @Column
    private Long size;

    @Column(name = "created_at",nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at",nullable = false, updatable = true)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

}
