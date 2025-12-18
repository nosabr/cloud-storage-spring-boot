CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       username VARCHAR(50) NOT NULL UNIQUE,
                       email VARCHAR(100) UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(20) NOT NULL DEFAULT 'ROLE_USER',
                       enabled BOOLEAN NOT NULL DEFAULT TRUE,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Индексы для быстрого поиска
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);

-- Тестовый пользователь (пароль: password123)
INSERT INTO users (username, email, password, role)
VALUES (
           'admin',
           'admin@example.com',
           '$2a$10$XptfskVPRdFDhVBXLY3iZu9gNKR.X.0LlUKIkJfhMDq5FPIlMYqwK',
           'ROLE_ADMIN'
       );
-- Тестовый пользователь (пароль: password123)
INSERT INTO users (username, email, password, role)
VALUES (
           'user',
           'user@example.com',
           '$2a$10$XptfskVPRdFDhVBXLY3iZu9gNKR.X.0LlUKIkJfhMDq5FPIlMYqwK',
           'ROLE_ADMIN'
       );