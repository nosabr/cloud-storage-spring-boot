package org.example.cloudstorage1.dto;

import org.example.cloudstorage1.entity.FileType;

public record ResourceResponse (
        String path,
        String name,
        Long size,
        FileType type
) {
}
