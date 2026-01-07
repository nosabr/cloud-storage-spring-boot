
CREATE TABLE file_node (
                           id BIGSERIAL PRIMARY KEY,
                           name VARCHAR(255) NOT NULL,
                           type VARCHAR(10) NOT NULL CHECK (type IN ('FILE', 'FOLDER')),
                           parent_id BIGINT,
                           owner_id BIGINT NOT NULL,
                           storage_key VARCHAR(500),
                           size BIGINT,
                           mime_type VARCHAR(255),
                           created_at TIMESTAMP NOT NULL,
                           updated_at TIMESTAMP NOT NULL,

                           CONSTRAINT fk_file_node_parent
                               FOREIGN KEY (parent_id)
                                   REFERENCES file_node(id)
                                   ON DELETE CASCADE,

                           CONSTRAINT fk_file_node_owner
                               FOREIGN KEY (owner_id)
                                   REFERENCES users(id)
                                   ON DELETE CASCADE
);

--changeset sabr:003-create-file-node-indexes
CREATE INDEX idx_parent_id ON file_node(parent_id);
CREATE INDEX idx_owner_id ON file_node(owner_id);
CREATE INDEX idx_owner_parent ON file_node(owner_id, parent_id);

--changeset sabr:003-add-file-node-comments
COMMENT ON TABLE file_node IS 'Файлы и папки пользователей';
COMMENT ON COLUMN file_node.id IS 'Уникальный идентификатор';
COMMENT ON COLUMN file_node.name IS 'Имя файла или папки';
COMMENT ON COLUMN file_node.type IS 'Тип: FILE или FOLDER';
COMMENT ON COLUMN file_node.parent_id IS 'ID родительской папки (NULL для корневых)';
COMMENT ON COLUMN file_node.owner_id IS 'ID владельца (пользователя)';
COMMENT ON COLUMN file_node.storage_key IS 'Ключ в MinIO (NULL для папок)';
COMMENT ON COLUMN file_node.size IS 'Размер файла в байтах (NULL для папок)';
COMMENT ON COLUMN file_node.mime_type IS 'MIME-тип файла (NULL для папок)';
COMMENT ON COLUMN file_node.created_at IS 'Дата создания';
COMMENT ON COLUMN file_node.updated_at IS 'Дата последнего изменения';