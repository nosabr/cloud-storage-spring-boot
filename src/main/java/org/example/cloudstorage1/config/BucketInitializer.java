package org.example.cloudstorage1.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cloudstorage1.exception.StorageException;
import org.example.cloudstorage1.service.storage.BucketService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class BucketInitializer implements CommandLineRunner {

    private final String bucketName;
    private final BucketService bucketService;

    @Override
    public void run(String... args) throws StorageException {
        log.info("Initializing bucket {}...", bucketName);
        try {
            bucketService.createBucketIfNotExists(bucketName);
            log.info("Bucket {} initialized.", bucketName);
        } catch (StorageException e) {
            log.warn("Bucket {} initialization failed.", bucketName, e);
            throw new StorageException("Error while creating bucket", e);
        }
    }
}
