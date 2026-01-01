package org.example.cloudstorage1.config;

import lombok.extern.slf4j.Slf4j;
import org.example.cloudstorage1.exception.StorageException;
import org.example.cloudstorage1.service.storage.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BucketInitializer implements CommandLineRunner {

    private final String bucketName;
    private final FileStorageService fileStorageService;

    public BucketInitializer(@Value("${storage.bucket-name}") String bucketName,
                             FileStorageService fileStorageService) {
        this.bucketName = bucketName;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Initializing bucket {}...", bucketName);
        try {
            fileStorageService.createBucketIfNotExists(bucketName);
            log.info("Bucket {} initialized.", bucketName);
        } catch (StorageException e) {
            log.warn("Bucket {} initialization failed.", bucketName, e);
            throw new StorageException("Error while creating bucket", e);
        }
    }
}
