package org.example.cloudstorage1;

import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseIntegrationTest {

    private static final PostgreSQLContainer<?> postgres;
    private static final GenericContainer<?> minio;
    private static final GenericContainer<?> redis;

    static {
        postgres = new PostgreSQLContainer<>("postgres:15")
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test");
        postgres.start();

        minio = new GenericContainer<>(DockerImageName.parse("minio/minio:latest"))
                .withExposedPorts(9000)
                .withEnv("MINIO_ROOT_USER", "minioadmin")
                .withEnv("MINIO_ROOT_PASSWORD", "minioadmin")
                .withCommand("server /data");
        minio.start();

        redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
                .withExposedPorts(6379);
        redis.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // PostgreSQL
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        // MinIO
        registry.add("storage.minio.endpoint", () ->
                "http://" + minio.getHost() + ":" + minio.getMappedPort(9000));
        registry.add("storage.access-key", () -> "minioadmin");
        registry.add("storage.secret-key", () -> "minioadmin");
        registry.add("storage.bucket-name", () -> "test-bucket");

        // Redis
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));

        createBucket();
    }

    private static void createBucket() {
        try {
            MinioClient client = MinioClient.builder()
                    .endpoint("http://" + minio.getHost() + ":" + minio.getMappedPort(9000))
                    .credentials("minioadmin", "minioadmin")
                    .build();

            client.makeBucket(MakeBucketArgs.builder()
                    .bucket("test-bucket")
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Failed to create test bucket", e);
        }
    }
}