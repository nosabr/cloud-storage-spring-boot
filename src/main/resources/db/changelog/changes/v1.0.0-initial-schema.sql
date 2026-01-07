CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       username VARCHAR(50) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(20) NOT NULL DEFAULT 'ROLE_USER',
                       enabled BOOLEAN NOT NULL DEFAULT TRUE,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Индексы для быстрого поиска
CREATE INDEX idx_users_username ON users(username);

-- Тестовый пользователь (пароль: password123)
INSERT INTO users (username, password, role)
VALUES (
           'admin',
           '$2a$10$XptfskVPRdFDhVBXLY3iZu9gNKR.X.0LlUKIkJfhMDq5FPIlMYqwK',
           'ROLE_ADMIN'
       );
-- Тестовый пользователь (пароль: password123)
INSERT INTO users (username, password, role)
VALUES (
           'user',
           '$2a$10$XptfskVPRdFDhVBXLY3iZu9gNKR.X.0LlUKIkJfhMDq5FPIlMYqwK',
           'ROLE_ADMIN'
       );


