package org.example.cloudstorage1.dto;

import org.example.cloudstorage1.repository.FileMetadataRepository;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record ParsedFile (
        MultipartFile file,
        String path,
        List<String> segments,
        String uuid
) {
    public String getFileName() {
        return segments.get(segments.size()-1);
    }

    public String getPathWithoutFileName() {
        return path;
    }
}
