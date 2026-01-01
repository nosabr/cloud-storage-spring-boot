# План разработки проекта "Облачное хранилище файлов"

> **Как использовать чеклисты:** В Markdown чекбоксы создаются так:
> - `- [ ]` - невыполненная задача
> - `- [x]` - выполненная задача (будет зачеркнута в большинстве редакторов)
>
> В GitHub, GitLab, многих редакторах (VS Code, Obsidian, Notion) можно кликать по чекбоксам для их переключения!

## Общая информация о проекте

**Тип проекта:** Многопользовательское файловое облако (аналог Google Drive)

**Основные технологии:**
- Java, Spring Boot (Web, Security, Data JPA, Session)
- PostgreSQL, Redis, MinIO (S3)
- Docker, Docker Compose
- React (готовый фронтенд)
- Maven/Gradle
- JUnit, Testcontainers

**Общая оценка времени:** 3-4 недели

---

## Этап 1: Подготовка инфраструктуры и базовая настройка

**Время:** 1-2 дня

### 1.1 Инициализация проекта

- [ ] Создать Spring Boot проект с необходимыми зависимостями:
    - Spring Web
    - Spring Security
    - Spring Data JPA
    - Spring Session
    - Lombok
    - PostgreSQL Driver
    - Minio SDK
    - Swagger/OpenAPI (SpringDoc)
    - Validation

### 1.2 Docker Compose - PostgreSQL

- [ ] Создать `docker-compose.yml`
- [ ] Добавить сервис PostgreSQL:
  ```yaml
  services:
    postgres:
      image: postgres:15
      environment:
        POSTGRES_DB: cloud_storage
        POSTGRES_USER: user
        POSTGRES_PASSWORD: password
      ports:
        - "5432:5432"
      volumes:
        - postgres_data:/var/lib/postgresql/data
  
  volumes:
    postgres_data:
  ```
- [x] Проверить подключение к БД

### 1.3 Базовая настройка проекта

- [ ] Настроить `application.yml`:
    - Подключение к PostgreSQL
    - Настройки JPA (ddl-auto, show-sql)
    - Настройки логирования
    - Server port
- [ ] Создать базовую структуру пакетов:
    - `controller`
    - `service`
    - `repository`
    - `entity`
    - `dto`
    - `config`
    - `exception`

---

## Этап 2: Пользователи и аутентификация

**Время:** 3-4 дня

### 2.1 Проектирование БД

- [ ] Разработать схему таблицы `users`:
  ```sql
  CREATE TABLE users (
      id BIGSERIAL PRIMARY KEY,
      username VARCHAR(50) UNIQUE NOT NULL,
      password VARCHAR(255) NOT NULL,
      enabled BOOLEAN DEFAULT TRUE,
      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
  );
  
  CREATE INDEX idx_users_username ON users(username);
  ```

### 2.2 Миграции БД

- [x] Добавить зависимость Liquibase или Flyway
- [x] Создать первую миграцию для таблицы users
- [x] Настроить путь к миграциям в `application.yml`
- [x] Проверить применение миграции

### 2.3 Spring Security и Spring Data JPA

- [x] Создать Entity `User`:
    - Аннотации JPA
    - Реализация UserDetails (если нужно)

- [x] Создать Repository `UserRepository`:
    - `Optional<User> findByUsername(String username)`
    - `boolean existsByUsername(String username)`

- [x] Реализовать `UserDetailsService`:
    - Загрузка пользователя по username

- [x] Настроить Spring Security:
    - Создать `SecurityConfig`
    - Настроить хеширование паролей (BCrypt)
    - Настроить доступ к endpoints:
        - `/api/auth/**` - permitAll
        - `/api/**` - authenticated
        - Статика - permitAll

### 2.4 Регистрация и авторизация

- [x] Создать DTO:
    - `SignUpRequest` (username, password)
    - `SignInRequest` (username, password)
    - `UserResponse` (username)
    - `ErrorResponse` (message)

- [x] Создать `AuthService`:
    - `UserResponse signUp(SignUpRequest request)`
    - `UserResponse signIn(SignInRequest request)`
    - `void signOut()`

- [x] Создать `AuthController`:
    - `POST /api/auth/sign-up` - регистрация
    - `POST /api/auth/sign-in` - авторизация
    - `POST /api/auth/sign-out` - выход

- [x] Реализовать валидацию:
    - Username: 3-50 символов, только латиница и цифры
    - Password: минимум 6 символов

- [x] Создать глобальный обработчик исключений:
    - 400 - ошибки валидации
    - 401 - неверные credentials
    - 409 - username занят
    - 500 - внутренняя ошибка

### 2.5 Endpoint текущего пользователя

- [ ] Создать `UserController`:
    - `GET /api/user/me` - информация о текущем пользователе

- [ ] Создать утилиту для получения текущего пользователя:
  ```java
  public static User getCurrentUser() {
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      // ...
  }
  ```

---

## Этап 3: Интеграционные тесты для пользователей

**Время:** 1-2 дня

### 3.1 Настройка Testcontainers

- [x] Добавить зависимости:
    - `testcontainers`
    - `postgresql` testcontainer
    - `spring-boot-starter-test`

- [x] Создать базовый тестовый класс:
  ```java
  @SpringBootTest
  @Testcontainers
  @AutoConfigureMockMvc
  class BaseIntegrationTest {
      @Container
      static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");
      
      @DynamicPropertySource
      static void configureProperties(DynamicPropertyRegistry registry) {
          registry.add("spring.datasource.url", postgres::getJdbcUrl);
          // ...
      }
  }
  ```

### 3.2 Написание тестов

- [x] Тесты `AuthService`:
    - Успешная регистрация пользователя
    - Регистрация с существующим username (ошибка 409)
    - Регистрация с невалидными данными
    - Успешная авторизация
    - Авторизация с неверным паролем
    - Авторизация несуществующего пользователя

- [x] Тесты `UserRepository`:
    - Поиск по username
    - Проверка существования username

- [x] Интеграционные тесты через MockMvc:
    - POST /api/auth/sign-up
    - POST /api/auth/sign-in
    - GET /api/user/me

---

## Этап 4: Файловое хранилище MinIO

**Время:** 3-4 дня

### 4.1 Docker Compose - MinIO

- [x] Добавить сервис MinIO в `docker-compose.yml`:
  ```yaml
  minio:
    image: minio/minio
    command: server /data --console-address ":9001"
    environment:
      MINIO_ROOT_USER: minioadmin
      MINIO_ROOT_PASSWORD: minioadmin
    ports:
      - "9000:9000"
      - "9001:9001"
    volumes:
      - minio_data:/data
  ```

  - [x] Запустить и проверить доступ к консоли (http://localhost:9001)

### 4.2 Интеграция MinIO SDK

- [ ] Добавить зависимость:
  ```xml
  <dependency>
      <groupId>io.minio</groupId>
      <artifactId>minio</artifactId>
      <version>8.5.7</version>
  </dependency>
  ```

- [ ] Создать `MinioConfig`:
  ```java
  @Bean
  public MinioClient minioClient() {
      return MinioClient.builder()
          .endpoint("http://localhost:9000")
          .credentials("minioadmin", "minioadmin")
          .build();
  }
  ```

- [x] Настроить параметры в `application.yml`:
    - minio.url
    - minio.access-key
    - minio.secret-key
    - minio.bucket-name

### 4.3 Базовые операции с файлами

- [ ] Создать `MinioService`:
    - `void createBucketIfNotExists(String bucketName)`
    - `void uploadFile(String objectName, InputStream stream, long size, String contentType)`
    - `InputStream downloadFile(String objectName)`
    - `void deleteFile(String objectName)`
    - `void moveFile(String source, String destination)`
    - `List<String> listFiles(String prefix)`
    - `boolean fileExists(String objectName)`
    - `ObjectStat getFileMetadata(String objectName)`

- [ ] Создать утилиту для работы с путями:
    - Генерация корневой папки: `user-${userId}-files/`
    - Объединение путей
    - Валидация путей
    - Извлечение имени файла из пути

- [ ] Реализовать инициализацию bucket при старте приложения:
  ```java
  @PostConstruct
  public void init() {
      createBucketIfNotExists("user-files");
  }
  ```

### 4.4 Создание сервиса изоляции пользователей

- [ ] Создать `FileStorageService`:
    - Преобразование пользовательских путей в MinIO пути
    - Проверка принадлежности файла пользователю
    - Валидация путей (нет `..`, начинается не с `/`)
    - Работа с папками (создание маркерных файлов)

---

## Этап 5: REST API для файлов и папок

**Время:** 4-5 дней

### 5.1 Модели и DTO

- [ ] Создать DTO:
  ```java
  public class ResourceDTO {
      private String path;
      private String name;
      private Long size;  // null для папок
      private ResourceType type;  // FILE, DIRECTORY
  }
  ```

- [ ] Создать Enum `ResourceType`

### 5.2 Сервисный слой

- [ ] Создать `FileStorageService` (расширить):
    - `ResourceDTO getResource(User user, String path)`
    - `void deleteResource(User user, String path)`
    - `byte[] downloadFile(User user, String path)`
    - `byte[] downloadDirectory(User user, String path)` (ZIP)
    - `ResourceDTO moveResource(User user, String from, String to)`
    - `List<ResourceDTO> searchResources(User user, String query)`
    - `List<ResourceDTO> uploadFiles(User user, String path, List<MultipartFile> files)`
    - `List<ResourceDTO> listDirectory(User user, String path)`
    - `ResourceDTO createDirectory(User user, String path)`

- [ ] Создать утилиту для работы с ZIP:
    - Упаковка папки в ZIP
    - Рекурсивное добавление файлов

### 5.3 Контроллер ResourceController

- [ ] Создать `ResourceController`:

    - [ ] `GET /api/resource?path=...`
        - Получение информации о ресурсе
        - Валидация path
        - Возврат ResourceDTO
        - Обработка ошибок: 400, 401, 404, 500

    - [ ] `DELETE /api/resource?path=...`
        - Удаление ресурса
        - Возврат 204 No Content
        - Обработка ошибок: 400, 401, 404, 500

    - [ ] `GET /api/resource/download?path=...`
        - Скачивание файла или папки (ZIP)
        - Content-Type: application/octet-stream
        - Content-Disposition: attachment; filename="..."
        - Обработка ошибок: 400, 401, 404, 500

    - [ ] `GET /api/resource/move?from=...&to=...`
        - Переименование/перемещение ресурса
        - Валидация обоих путей
        - Проверка на существование целевого ресурса (409)
        - Возврат обновленного ResourceDTO
        - Обработка ошибок: 400, 401, 404, 409, 500

    - [ ] `GET /api/resource/search?query=...`
        - Поиск по имени
        - Возврат List<ResourceDTO>
        - Обработка ошибок: 400, 401, 500

    - [ ] `POST /api/resource?path=...`
        - Upload файлов (multipart/form-data)
        - Поддержка множественных файлов
        - Поддержка вложенных папок
        - Возврат 201 Created + List<ResourceDTO>
        - Обработка ошибок: 400, 401, 409, 500

### 5.4 Контроллер DirectoryController

- [ ] Создать `DirectoryController`:

    - [ ] `GET /api/directory?path=...`
        - Получение содержимого папки
        - Возврат List<ResourceDTO>
        - Обработка ошибок: 400, 401, 404, 500

    - [ ] `POST /api/directory?path=...`
        - Создание пустой папки
        - Возврат 201 Created + ResourceDTO
        - Обработка ошибок: 400, 401, 404, 409, 500

### 5.5 Обработка исключений

- [ ] Создать кастомные исключения:
    - `ResourceNotFoundException`
    - `ResourceAlreadyExistsException`
    - `InvalidPathException`
    - `AccessDeniedException`

- [ ] Расширить `GlobalExceptionHandler`:
    - Обработка всех кастомных исключений
    - Возврат ErrorResponse с правильными HTTP кодами
    - Логирование ошибок

### 5.6 Валидация путей

- [ ] Создать утилиту `PathValidator`:
    - Проверка на null/empty
    - Проверка на недопустимые символы
    - Проверка на `..` (path traversal)
    - Проверка на абсолютные пути (не должны начинаться с `/`)
    - Проверка формата пути к папке (должен заканчиваться на `/`)

---

## Этап 6: Продвинутые функции

**Время:** 2-3 дня

### 6.1 Скачивание папок (ZIP)

- [ ] Создать `ZipService`:
    - Метод создания ZIP из списка файлов
    - Рекурсивное добавление файлов из подпапок
    - Потоковая передача (stream) для больших файлов
    - Сохранение структуры папок в архиве

- [ ] Интегрировать в `downloadDirectory`:
    - Получить все файлы из папки (рекурсивно)
    - Создать ZIP
    - Вернуть как byte[] или InputStream

### 6.2 Загрузка папок

- [ ] Обработка MultipartFile с путями:
    - Извлечение структуры папок из имени файла
    - Создание промежуточных папок
    - Загрузка файлов с сохранением структуры

- [ ] Пример: файл `folder1/folder2/file.txt` при загрузке в `root/`:
    - Создать `root/folder1/`
    - Создать `root/folder1/folder2/`
    - Загрузить файл в `root/folder1/folder2/file.txt`

### 6.3 Поиск файлов

- [ ] Реализовать поиск:
    - Поиск по префиксу корневой папки пользователя
    - Фильтрация по имени (contains, ignoreCase)
    - Возврат полных путей к найденным ресурсам
    - Ограничение результатов (например, 100 файлов)

### 6.4 Работа с пустыми папками в MinIO

- [ ] Решить проблему пустых папок:
    - MinIO не хранит пустые папки
    - Создавать маркерный файл `.keep` или `{folder_name}/.minio.sys/`
    - Скрывать маркерные файлы при получении списка файлов
    - Удалять маркерные файлы при удалении папки

---

## Этап 7: Swagger документация

**Время:** 1 день

### 7.1 Настройка Swagger

- [ ] Добавить зависимость:
  ```xml
  <dependency>
      <groupId>org.springdoc</groupId>
      <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
      <version>2.2.0</version>
  </dependency>
  ```

- [ ] Настроить в `application.yml`:
  ```yaml
  springdoc:
    api-docs:
      path: /api-docs
    swagger-ui:
      path: /swagger-ui.html
  ```

- [ ] Создать конфигурацию:
  ```java
  @Configuration
  public class OpenApiConfig {
      @Bean
      public OpenAPI customOpenAPI() {
          return new OpenAPI()
              .info(new Info()
                  .title("Cloud File Storage API")
                  .version("1.0")
                  .description("REST API for cloud file storage"));
      }
  }
  ```

### 7.2 Документирование API

- [ ] Добавить аннотации на контроллеры:
    - `@Tag(name = "Authentication", description = "User authentication operations")`
    - `@Tag(name = "Resources", description = "File and folder operations")`

- [ ] Добавить аннотации на методы:
    - `@Operation(summary = "Register new user")`
    - `@ApiResponses` для всех возможных ответов
    - `@Parameter` для параметров запроса
    - `@Schema` для DTO

- [ ] Примеры аннотаций:
  ```java
  @Operation(summary = "Upload files", description = "Upload one or more files to specified path")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Files uploaded successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid request"),
      @ApiResponse(responseCode = "401", description = "Not authenticated"),
      @ApiResponse(responseCode = "409", description = "File already exists"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @PostMapping("/resource")
  public ResponseEntity<List<ResourceDTO>> uploadFiles(
      @Parameter(description = "Path to directory") @RequestParam String path,
      @Parameter(description = "Files to upload") @RequestParam List<MultipartFile> files
  ) { ... }
  ```

### 7.3 Проверка через Swagger UI

- [ ] Запустить приложение
- [ ] Открыть http://localhost:8080/swagger-ui.html
- [ ] Протестировать все endpoints:
    - Регистрация
    - Авторизация
    - Создание папки
    - Загрузка файла
    - Скачивание файла
    - Удаление файла
    - Поиск

---

## Этап 8: Интеграция Redis для сессий

**Время:** 1-2 дня

### 8.1 Docker Compose - Redis

- [ ] Добавить сервис Redis в `docker-compose.yml`:
  ```yaml
  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
  ```

- [ ] Запустить и проверить подключение

### 8.2 Spring Session

- [ ] Добавить зависимости:
  ```xml
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-data-redis</artifactId>
  </dependency>
  <dependency>
      <groupId>org.springframework.session</groupId>
      <artifactId>spring-session-data-redis</artifactId>
  </dependency>
  ```

- [ ] Настроить в `application.yml`:
  ```yaml
  spring:
    session:
      store-type: redis
      redis:
        namespace: spring:session
      timeout: 3600  # 1 час
    redis:
      host: localhost
      port: 6379
  ```

- [ ] Добавить аннотацию на главный класс:
  ```java
  @EnableRedisHttpSession
  ```

### 8.3 Тестирование сессий

- [ ] Проверить создание сессии при авторизации
- [ ] Проверить сохранение сессии в Redis:
  ```bash
  redis-cli
  KEYS *
  GET spring:session:sessions:...
  ```
- [ ] Проверить персистентность после рестарта приложения
- [ ] Проверить автоматическое удаление истекших сессий

---

## Этап 9: Интеграция фронтенда

**Время:** 1-2 дня

**Информация о фронтенде:**
- Репозиторий: https://github.com/zhukovsd/cloud-storage-frontend/
- Демо с mock API: https://zhukovsd.github.io/cloud-storage-frontend/files/
- Собранные файлы: https://github.com/zhukovsd/cloud-storage-frontend/tree/master/dist

### 9.1 Способ 1: Интеграция в Spring Boot (простой способ)

Самый простой способ - добавить статические файлы React приложения прямо в Spring Boot проект.

- [ ] Скачать собранные файлы:
    - Вариант 1: Склонировать репозиторий и взять файлы из папки `dist/`
    - Вариант 2: Скачать напрямую с GitHub

- [ ] Скопировать содержимое папки `dist/` в `src/main/resources/static/`:
  ```
  src/main/resources/static/
  ├── index.html
  ├── assets/
  │   ├── index-*.js
  │   ├── index-*.css
  │   └── ...
  ├── favicon.ico
  └── другие файлы
  ```

- [ ] Настроить Spring для SPA маршрутизации:
  ```java
  @Configuration
  public class WebConfig implements WebMvcConfigurer {
      @Override
      public void addViewControllers(ViewControllerRegistry registry) {
          // Главная страница
          registry.addViewController("/")
                  .setViewName("forward:/index.html");
          
          // Все маршруты без расширений перенаправляем на index.html
          // Это нужно для работы React Router
          registry.addViewController("/**/{path:[^\\.]*}")
                  .setViewName("forward:/index.html");
      }
  }
  ```

- [ ] Настроить Spring Security для доступа к статике:
  ```java
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
      http
          .authorizeHttpRequests(auth -> auth
              // Разрешаем доступ к статическим файлам
              .requestMatchers("/", "/index.html", "/assets/**", 
                             "/favicon.ico", "/manifest.json", "/*.js", "/*.css").permitAll()
              // API аутентификации
              .requestMatchers("/api/auth/**").permitAll()
              // Остальное API требует авторизации
              .requestMatchers("/api/**").authenticated()
              .anyRequest().authenticated()
          )
          // ... остальная конфигурация
      return http.build();
  }
  ```

- [ ] Убедиться, что API доступен под путём `/api`:
    - Все контроллеры должны иметь `@RequestMapping("/api")`
    - Примеры: `/api/auth/sign-up`, `/api/resource`, `/api/directory`

- [ ] **Важно**: При таком способе интеграции фронтенд и бэкенд работают на одном порту (например, 8080)

### 9.2 Способ 2: Docker Nginx (рекомендуется для продакшена)

Этот способ позволяет отделить фронтенд от бэкенда и использовать Nginx для раздачи статики.

#### 9.2.1 Подготовка фронтенда

- [ ] Склонировать репозиторий фронтенда:
  ```bash
  git clone https://github.com/zhukovsd/cloud-storage-frontend.git
  cd cloud-storage-frontend
  ```

- [ ] Настроить адрес API бэкенда в `public/config.js`:
  ```javascript
  window.APP_CONFIG = {
    API_BASE_URL: 'http://localhost:8080'  // Для локальной разработки
    // API_BASE_URL: 'http://your-domain.com'  // Для продакшена
  };
  ```

#### 9.2.2 Создание Docker образа

- [ ] Создать `Dockerfile` в корне проекта фронтенда:
  ```dockerfile
  FROM nginx:alpine
  
  # Копируем собранное приложение
  COPY dist/ /usr/share/nginx/html/
  
  # Копируем конфигурацию Nginx
  COPY nginx.conf /etc/nginx/conf.d/default.conf
  
  EXPOSE 80
  
  CMD ["nginx", "-g", "daemon off;"]
  ```

- [ ] Создать `nginx.conf` в корне проекта фронтенда:
  ```nginx
  server {
      listen 80;
      server_name localhost;
      root /usr/share/nginx/html;
      index index.html;
  
      # Увеличиваем лимиты для загрузки файлов
      client_max_body_size 100M;
  
      # SPA - все маршруты на index.html
      location / {
          try_files $uri $uri/ /index.html;
      }
  
      # Проксирование API запросов к бэкенду
      location /api {
          proxy_pass http://backend:8080;
          proxy_set_header Host $host;
          proxy_set_header X-Real-IP $remote_addr;
          proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
          proxy_set_header X-Forwarded-Proto $scheme;
          
          # Для больших файлов
          proxy_request_buffering off;
          proxy_http_version 1.1;
      }
  
      # Кеширование статических файлов
      location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
          expires 1y;
          add_header Cache-Control "public, immutable";
      }
  }
  ```

- [ ] Собрать Docker образ:
  ```bash
  docker build -t cloud-storage-frontend:latest .
  ```

#### 9.2.3 Интеграция в Docker Compose

- [ ] Обновить `docker-compose.yml`, добавив фронтенд:
  ```yaml
  version: '3.8'
  
  services:
    frontend:
      image: cloud-storage-frontend:latest
      restart: always
      ports:
        - "80:80"
      depends_on:
        - backend
      networks:
        - app-network
  
    backend:
      # Ваш Spring Boot сервис
      build: .
      container_name: backend
      restart: always
      ports:
        - "8080:8080"
      environment:
        - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/cloud_storage
        - SPRING_REDIS_HOST=redis
        - MINIO_URL=http://minio:9000
      depends_on:
        - postgres
        - redis
        - minio
      networks:
        - app-network
  
    postgres:
      image: postgres:15
      restart: always
      environment:
        POSTGRES_DB: cloud_storage
        POSTGRES_USER: user
        POSTGRES_PASSWORD: password
      volumes:
        - postgres_data:/var/lib/postgresql/data
      networks:
        - app-network
  
    redis:
      image: redis:7-alpine
      restart: always
      volumes:
        - redis_data:/data
      networks:
        - app-network
  
    minio:
      image: minio/minio:latest
      restart: always
      command: server /data --console-address ":9001"
      environment:
        MINIO_ROOT_USER: minioadmin
        MINIO_ROOT_PASSWORD: minioadmin
      volumes:
        - minio_data:/data
      ports:
        - "9000:9000"
        - "9001:9001"
      networks:
        - app-network
  
  volumes:
    postgres_data:
    redis_data:
    minio_data:
  
  networks:
    app-network:
      driver: bridge
  ```

- [ ] Запустить весь стек:
  ```bash
  docker-compose up -d
  ```

- [ ] Фронтенд будет доступен на http://localhost:80
- [ ] Бэкенд API на http://localhost:8080

#### 9.2.4 Преимущества Docker Nginx подхода

- ✅ Разделение фронтенда и бэкенда
- ✅ Лучшая производительность раздачи статики (Nginx оптимизирован для этого)
- ✅ Возможность независимого масштабирования компонентов
- ✅ Возможность независимого обновления фронтенда и бэкенда
- ✅ Удобство для продакшн деплоя
- ✅ Nginx может работать как балансировщик нагрузки

### 9.3 CORS настройки (для Docker Nginx способа)

При разделении фронтенда и бэкенда необходимо настроить CORS:

- [ ] Создать конфигурацию CORS в Spring Boot:
  ```java
  @Configuration
  public class CorsConfig {
      
      @Bean
      public CorsFilter corsFilter() {
          CorsConfiguration config = new CorsConfiguration();
          
          // Разрешаем cookies
          config.setAllowCredentials(true);
          
          // Разрешенные origins
          config.addAllowedOrigin("http://localhost:80");
          config.addAllowedOrigin("http://localhost:3000");  // Для разработки
          
          // Разрешаем все headers
          config.addAllowedHeader("*");
          
          // Разрешаем все HTTP методы
          config.addAllowedMethod("*");
          
          // Применяем к API endpoints
          UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
          source.registerCorsConfiguration("/api/**", config);
          
          return new CorsFilter(source);
      }
  }
  ```

- [ ] Альтернатива - настроить CORS в Spring Security:
  ```java
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
      http
          .cors(cors -> cors.configurationSource(corsConfigurationSource()))
          // ... остальная конфигурация
      return http.build();
  }
  
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
      CorsConfiguration configuration = new CorsConfiguration();
      configuration.setAllowedOrigins(Arrays.asList("http://localhost:80", "http://localhost:3000"));
      configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
      configuration.setAllowedHeaders(Arrays.asList("*"));
      configuration.setAllowCredentials(true);
      
      UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
      source.registerCorsConfiguration("/**", configuration);
      return source;
  }
  ```

### 9.5 Настройка лимитов загрузки

- [ ] Увеличить лимиты в `application.yml`:
  ```yaml
  spring:
    servlet:
      multipart:
        max-file-size: 100MB
        max-request-size: 100MB
  ```

### 9.6 Тестирование интеграции

- [ ] Запустить приложение
- [ ] Открыть http://localhost:8080
- [ ] Протестировать:
    - Регистрацию нового пользователя
    - Авторизацию
    - Загрузку файлов
    - Создание папок
    - Навигацию по папкам
    - Скачивание файлов
    - Удаление ресурсов
    - Переименование
    - Поиск
    - Logout

---

## Этап 10: Интеграционные тесты файлов (опционально)

**Время:** 2-3 дня

### 10.1 Testcontainers для MinIO

- [ ] Создать GenericContainer для MinIO:
  ```java
  @Container
  static GenericContainer<?> minioContainer = new GenericContainer<>("minio/minio:latest")
      .withCommand("server", "/data")
      .withExposedPorts(9000)
      .withEnv("MINIO_ROOT_USER", "minioadmin")
      .withEnv("MINIO_ROOT_PASSWORD", "minioadmin");
  ```

- [ ] Настроить MinIO client для тестов:
  ```java
  @DynamicPropertySource
  static void configureMinioProperties(DynamicPropertyRegistry registry) {
      registry.add("minio.url", 
          () -> "http://localhost:" + minioContainer.getMappedPort(9000));
      registry.add("minio.access-key", () -> "minioadmin");
      registry.add("minio.secret-key", () -> "minioadmin");
  }
  ```

### 10.2 Тесты операций с файлами

- [ ] Тест загрузки файла:
    - Создать тестовый файл
    - Загрузить через сервис
    - Проверить существование в MinIO
    - Проверить содержимое

- [ ] Тест удаления файла:
    - Загрузить файл
    - Удалить через сервис
    - Проверить отсутствие в MinIO

- [ ] Тест переименования файла:
    - Загрузить файл
    - Переименовать
    - Проверить новое имя
    - Проверить отсутствие старого имени

- [ ] Тест перемещения файла:
    - Создать папки
    - Загрузить файл в одну папку
    - Переместить в другую
    - Проверить местоположение

- [ ] Тест прав доступа:
    - Создать двух пользователей
    - Загрузить файл от первого пользователя
    - Попытаться получить доступ от второго
    - Ожидать AccessDeniedException

- [ ] Тест поиска:
    - Создать несколько файлов
    - Найти по запросу
    - Проверить результаты
    - Проверить изоляцию пользователей

- [ ] Тест скачивания папки (ZIP):
    - Создать папку с файлами
    - Скачать как ZIP
    - Проверить содержимое архива

### 10.3 Тесты через MockMvc

- [ ] Интеграционные тесты endpoints:
    - POST /api/resource (upload)
    - GET /api/resource (info)
    - DELETE /api/resource
    - GET /api/resource/download
    - GET /api/resource/move
    - GET /api/resource/search
    - GET /api/directory
    - POST /api/directory

---

## Этап 11: Деплой

**Время:** 2-3 дня

### 11.1 Подготовка к деплою

- [ ] Создать production профиль (`application-prod.yml`):
  ```yaml
  spring:
    datasource:
      url: ${DATABASE_URL}
      username: ${DATABASE_USERNAME}
      password: ${DATABASE_PASSWORD}
    redis:
      host: ${REDIS_HOST}
      port: ${REDIS_PORT}
  
  minio:
    url: ${MINIO_URL}
    access-key: ${MINIO_ACCESS_KEY}
    secret-key: ${MINIO_SECRET_KEY}
  
  server:
    port: 8080
  ```

- [ ] Собрать JAR артефакт:
  ```bash
  ./mvnw clean package -DskipTests
  # или
  ./gradlew build -x test
  ```

- [ ] Проверить работоспособность локально:
  ```bash
  java -jar target/cloud-storage-0.0.1-SNAPSHOT.jar
  ```

### 11.2 Выбор и настройка сервера

- [ ] Выбрать хостинг-провайдера:
    - DigitalOcean (рекомендуется)
    - AWS EC2
    - Hetzner
    - Linode
    - VDS/VPS

- [ ] Создать сервер (минимальные требования):
    - 2 GB RAM
    - 2 vCPU
    - 50 GB SSD
    - Ubuntu 22.04 LTS

- [ ] Подключиться по SSH:
  ```bash
  ssh root@your_server_ip
  ```

### 11.3 Установка необходимого ПО

- [ ] Обновить систему:
  ```bash
  apt update && apt upgrade -y
  ```

- [ ] Установить JRE:
  ```bash
  apt install openjdk-17-jre-headless -y
  java -version
  ```

- [ ] Установить Docker:
  ```bash
  curl -fsSL https://get.docker.com -o get-docker.sh
  sh get-docker.sh
  systemctl enable docker
  systemctl start docker
  docker --version
  ```

- [ ] Установить Docker Compose:
  ```bash
  apt install docker-compose -y
  docker-compose --version
  ```

### 11.4 Развертывание приложения

- [ ] Создать директорию для проекта:
  ```bash
  mkdir -p /opt/cloud-storage
  cd /opt/cloud-storage
  ```

- [ ] Создать `docker-compose.yml` для production:
  ```yaml
  version: '3.8'
  
  services:
    postgres:
      image: postgres:15
      restart: always
      environment:
        POSTGRES_DB: cloud_storage
        POSTGRES_USER: clouduser
        POSTGRES_PASSWORD: ${DB_PASSWORD}
      volumes:
        - postgres_data:/var/lib/postgresql/data
      networks:
        - app-network
  
    redis:
      image: redis:7-alpine
      restart: always
      volumes:
        - redis_data:/data
      networks:
        - app-network
  
    minio:
      image: minio/minio:latest
      restart: always
      command: server /data --console-address ":9001"
      environment:
        MINIO_ROOT_USER: ${MINIO_ROOT_USER}
        MINIO_ROOT_PASSWORD: ${MINIO_ROOT_PASSWORD}
      volumes:
        - minio_data:/data
      ports:
        - "9000:9000"
        - "9001:9001"
      networks:
        - app-network
  
  volumes:
    postgres_data:
    redis_data:
    minio_data:
  
  networks:
    app-network:
      driver: bridge
  ```

- [ ] Создать `.env` файл:
  ```bash
  DB_PASSWORD=your_secure_password
  MINIO_ROOT_USER=admin
  MINIO_ROOT_PASSWORD=your_secure_password
  ```

- [ ] Скопировать JAR на сервер:
  ```bash
  # На локальной машине
  scp target/cloud-storage-0.0.1-SNAPSHOT.jar root@your_server_ip:/opt/cloud-storage/
  ```

- [ ] Запустить Docker Compose:
  ```bash
  docker-compose up -d
  ```

- [ ] Создать systemd сервис для Spring Boot:
  ```bash
  nano /etc/systemd/system/cloud-storage.service
  ```

  Содержимое:
  ```ini
  [Unit]
  Description=Cloud Storage Application
  After=docker.service
  Requires=docker.service
  
  [Service]
  User=root
  WorkingDirectory=/opt/cloud-storage
  ExecStart=/usr/bin/java -jar \
    -Dspring.profiles.active=prod \
    -DDATABASE_URL=jdbc:postgresql://localhost:5432/cloud_storage \
    -DDATABASE_USERNAME=clouduser \
    -DDATABASE_PASSWORD=your_secure_password \
    -DREDIS_HOST=localhost \
    -DREDIS_PORT=6379 \
    -DMINIO_URL=http://localhost:9000 \
    -DMINIO_ACCESS_KEY=admin \
    -DMINIO_SECRET_KEY=your_secure_password \
    /opt/cloud-storage/cloud-storage-0.0.1-SNAPSHOT.jar
  SuccessExitStatus=143
  TimeoutStopSec=10
  Restart=on-failure
  RestartSec=5
  
  [Install]
  WantedBy=multi-user.target
  ```

- [ ] Запустить приложение:
  ```bash
  systemctl daemon-reload
  systemctl enable cloud-storage
  systemctl start cloud-storage
  systemctl status cloud-storage
  ```

- [ ] Проверить логи:
  ```bash
  journalctl -u cloud-storage -f
  ```

### 11.5 Настройка безопасности

- [ ] Настроить firewall (UFW):
  ```bash
  ufw allow 22/tcp      # SSH
  ufw allow 80/tcp      # HTTP
  ufw allow 443/tcp     # HTTPS
  ufw allow 8080/tcp    # Spring Boot (временно)
  ufw enable
  ufw status
  ```

- [ ] Установить и настроить Nginx (reverse proxy):
  ```bash
  apt install nginx -y
  ```

- [ ] Создать конфигурацию Nginx:
  ```bash
  nano /etc/nginx/sites-available/cloud-storage
  ```

  Содержимое:
  ```nginx
  server {
      listen 80;
      server_name your_domain.com;
  
      client_max_body_size 100M;
  
      location / {
          proxy_pass http://localhost:8080;
          proxy_set_header Host $host;
          proxy_set_header X-Real-IP $remote_addr;
          proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
          proxy_set_header X-Forwarded-Proto $scheme;
      }
  }
  ```

- [ ] Активировать конфигурацию:
  ```bash
  ln -s /etc/nginx/sites-available/cloud-storage /etc/nginx/sites-enabled/
  nginx -t
  systemctl restart nginx
  ```

- [ ] Установить SSL сертификат (Let's Encrypt):
  ```bash
  apt install certbot python3-certbot-nginx -y
  certbot --nginx -d your_domain.com
  ```

- [ ] Закрыть порт 8080:
  ```bash
  ufw delete allow 8080/tcp
  ```

### 11.6 Мониторинг и обслуживание

- [ ] Настроить логирование:
    - Проверить логи приложения: `journalctl -u cloud-storage`
    - Проверить логи Nginx: `tail -f /var/log/nginx/error.log`
    - Проверить логи Docker: `docker-compose logs -f`

- [ ] Настроить ротацию логов
- [ ] Настроить мониторинг места на диске
- [ ] Настроить бэкапы:
    - PostgreSQL: `pg_dump`
    - MinIO: копирование volume
    - Redis: RDB snapshots

### 11.7 Проверка работоспособности

- [ ] Открыть http://your_domain.com или http://your_server_ip
- [ ] Протестировать все функции:
    - Регистрация
    - Авторизация
    - Загрузка файлов
    - Скачивание файлов
    - Удаление
    - Переименование
    - Поиск

---

## Этап 12: Финальная проверка и рефакторинг

**Время:** 1-2 дня

### 12.1 Проверка по чеклисту из ТЗ

- [ ] **Функциональные проблемы:**
    - [ ] Соответствие формата API требуемому
    - [ ] Правильная обработка ошибок (коды 400, 401, 404, 409, 500)
    - [ ] Валидация имён файлов и папок
    - [ ] Защита от затирания файлов при переименовании
    - [ ] Корректность файлов и архивов при скачивании
    - [ ] Обработка несуществующих папок (404)
    - [ ] Изоляция пользователей (невозможность доступа к чужим файлам)
    - [ ] Адекватные лимиты на размер загружаемых файлов

- [ ] **"Протекание" деталей реализации:**
    - [ ] Путь к файлу НЕ содержит `user-${id}-files`
    - [ ] Работа с пустыми папками скрыта от пользователя
    - [ ] Маркерные файлы не видны в UI

### 12.2 Код-ревью и рефакторинг

- [ ] Проверить именование:
    - Классы: PascalCase
    - Методы/переменные: camelCase
    - Константы: UPPER_SNAKE_CASE
    - Пакеты: lowercase

- [ ] Проверить структуру проекта:
    - Правильное разделение по слоям
    - Отсутствие циклических зависимостей
    - Использование интерфейсов где нужно

- [ ] Проверить обработку исключений:
    - Все исключения перехватываются
    - Правильные HTTP коды
    - Информативные сообщения об ошибках

- [ ] Проверить безопасность:
    - Пароли хешируются
    - SQL-инъекции невозможны (JPA)
    - Path traversal защищен
    - CSRF защита настроена

- [ ] Оптимизация:
    - Нет N+1 запросов
    - Индексы в БД на нужных полях
    - Потоковая передача больших файлов
    - Правильная работа с connection pool

### 12.3 Документация

- [ ] Создать подробный README.md:
  ```markdown
  # Cloud File Storage
  
  ## Описание
  Многопользовательское файловое облако с REST API.
  
  ## Технологии
  - Java 17, Spring Boot 3.x
  - PostgreSQL, Redis, MinIO
  - Docker, Docker Compose
  - React (frontend)
  
  ## Требования
  - JDK 17+
  - Docker и Docker Compose
  - Maven/Gradle
  
  ## Запуск локально
  1. Клонировать репозиторий
  2. Запустить Docker Compose: `docker-compose up -d`
  3. Запустить приложение: `./mvnw spring-boot:run`
  4. Открыть http://localhost:8080
  
  ## API документация
  Swagger UI: http://localhost:8080/swagger-ui.html
  
  ## Конфигурация
  См. application.yml
  
  ## Деплой
  См. DEPLOY.md
  
  ## Лицензия
  MIT
  ```

- [ ] Создать DEPLOY.md с инструкциями по деплою

- [ ] Добавить JavaDoc к публичным методам

- [ ] Создать примеры запросов (Postman collection или curl)

### 12.4 Тестирование

- [ ] Запустить все тесты:
  ```bash
  ./mvnw test
  # или
  ./gradlew test
  ```

- [ ] Проверить покрытие кода тестами
- [ ] Добавить недостающие тесты

### 12.5 Подготовка к публикации

- [ ] Создать .gitignore (исключить .env, *.log, target/, build/)
- [ ] Удалить временные файлы и комментарии
- [ ] Проверить отсутствие хардкод паролей
- [ ] Создать LICENSE файл
- [ ] Обновить версию в pom.xml/build.gradle

---

## Итоговый чеклист готовности проекта

### Функциональность
- [ ] Регистрация и авторизация работают
- [ ] Logout работает
- [ ] Загрузка файлов работает
- [ ] Загрузка папок (с вложенностью) работает
- [ ] Скачивание файлов работает
- [ ] Скачивание папок (ZIP) работает
- [ ] Удаление файлов и папок работает
- [ ] Переименование работает
- [ ] Перемещение работает
- [ ] Создание пустых папок работает
- [ ] Поиск файлов работает
- [ ] Навигация по папкам работает

### Безопасность
- [ ] Пароли хешируются
- [ ] Пользователи изолированы друг от друга
- [ ] Path traversal защищен
- [ ] SQL-инъекции невозможны
- [ ] Сессии хранятся в Redis
- [ ] HTTPS настроен (для production)

### Инфраструктура
- [ ] Docker Compose для всех зависимостей
- [ ] Миграции БД настроены
- [ ] Логирование настроено
- [ ] Swagger документация доступна
- [ ] Фронтенд интегрирован

### Код
- [ ] Код структурирован и читаем
- [ ] Нет дублирования
- [ ] Обработка ошибок везде
- [ ] Тесты написаны и проходят
- [ ] README.md заполнен

### Деплой
- [ ] Приложение задеплоено
- [ ] Доступно по HTTP/HTTPS
- [ ] Все функции работают на prod
- [ ] Логи доступны
- [ ] Мониторинг настроен

---

## Полезные ресурсы

### Документация
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Spring Security](https://spring.io/projects/spring-security)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Spring Session](https://spring.io/projects/spring-session)
- [MinIO Java SDK](https://min.io/docs/minio/linux/developers/java/minio-java.html)
- [Testcontainers](https://www.testcontainers.org/)

### Туториалы
- [Baeldung - Spring Security](https://www.baeldung.com/security-spring)
- [Baeldung - Spring Session](https://www.baeldung.com/spring-session)
- [Baeldung - File Upload](https://www.baeldung.com/spring-file-upload)

### Репозитории
- [Фронтенд](https://github.com/zhukovsd/cloud-storage-frontend)
- [Завершенные проекты](https://zhukovsd.github.io/java-backend-learning-course/finished-projects/cloud-file-storage/)

---

## Заключение

Этот план является гибким руководством. Вы можете адаптировать его под свои нужды, пропускать опциональные этапы или добавлять дополнительные функции.

**Ключевые советы:**
1. Делайте маленькие, инкрементальные шаги
2. Тестируйте после каждого этапа
3. Делайте коммиты часто
4. Не бойтесь обращаться за помощью в сообщество
5. Изучайте чужие реализации для вдохновения

Удачи в разработке! 🚀