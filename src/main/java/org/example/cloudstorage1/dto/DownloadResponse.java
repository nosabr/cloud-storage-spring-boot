package org.example.cloudstorage1.dto;

import org.springframework.core.io.Resource;

public record DownloadResponse (
        String name,
        Resource resource
) {
}
