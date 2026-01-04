package org.example.cloudstorage1.dto;

public record ResourceResponse (
        String path,
        String name,
        double size,
        ResourceType type
) {
}
