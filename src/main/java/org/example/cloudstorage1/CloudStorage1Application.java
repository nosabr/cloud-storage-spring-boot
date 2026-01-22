package org.example.cloudstorage1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SpringBootApplication
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 1800) // 30 минут
public class CloudStorage1Application {

    public static void main(String[] args) {
        SpringApplication.run(CloudStorage1Application.class, args);
    }
}
