START TRANSACTION;


create table if not exists test(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255)
);

#CREATE TABLE IF NOT EXISTS users
#(
#    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
#    user_id             VARCHAR(255) NOT NULL,
#    first_name          VARCHAR(255),
#    username            VARCHAR(255),
#    last_name           VARCHAR(255),
#    email               VARCHAR(255),
#    bio                 VARCHAR(255),
#    reference_id        VARCHAR(255),
#    qr_code_secret      VARCHAR(255),
#    qr_code_image_uri   TEXT,
#    image_url           VARCHAR(255),
#    last_login          TIMESTAMP(6)          DEFAULT CURRENT_TIMESTAMP(6),
#    login_attempts      INT                   DEFAULT 0,
#    mfa                 BOOLEAN      NOT NULL DEFAULT FALSE,
#    enabled             BOOLEAN      NOT NULL DEFAULT FALSE,
#    account_non_expired BOOLEAN      NOT NULL DEFAULT FALSE,
#    account_non_locked  BOOLEAN      NOT NULL DEFAULT FALSE,
#    created_by          BIGINT       NOT NULL,
#    updated_by          BIGINT       NOT NULL,
#    created_at          TIMESTAMP(6)          DEFAULT CURRENT_TIMESTAMP(6),
#    updated_at          TIMESTAMP(6)          DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
#    CONSTRAINT uq_users_email UNIQUE (email),
#    CONSTRAINT uq_users_user_id UNIQUE (user_id),
#    CONSTRAINT fk_users_created_by FOREIGN KEY (created_by) REFERENCES users (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE ,
#    CONSTRAINT fk_users_updated_by FOREIGN KEY (updated_by) REFERENCES users (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE
#);
#
#CREATE TABLE IF NOT EXISTS confirmations
#(
#    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
#    uuidKey      VARCHAR(255) UNIQUE,
#    user_id      BIGINT NOT NULL UNIQUE,
#    reference_id VARCHAR(255),
#    created_by   BIGINT NOT NULL,
#    updated_by   BIGINT NOT NULL,
#    created_at   TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
#    updated_at   TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
#    CONSTRAINT fk_confirmations_user_id FOREIGN KEY (user_id) REFERENCES users (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE,
#    CONSTRAINT fk_confirmations_created_by FOREIGN KEY (created_by) REFERENCES users (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE,
#    CONSTRAINT fk_confirmations_updated_by FOREIGN KEY (updated_by) REFERENCES users (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE
#);
#
#CREATE TABLE IF NOT EXISTS credentials
#(
#    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
#    password     VARCHAR(255) NOT NULL,
#    reference_id VARCHAR(255),
#    user_id      BIGINT       NOT NULL UNIQUE,
#    created_by   BIGINT       NOT NULL,
#    updated_by   BIGINT       NOT NULL,
#    created_at   TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
#    updated_at   TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
#    CONSTRAINT fk_credentials_user_id FOREIGN KEY (user_id) REFERENCES users (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE,
#    CONSTRAINT fk_credentials_created_by FOREIGN KEY (created_by) REFERENCES users (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE,
#    CONSTRAINT fk_credentials_updated_by FOREIGN KEY (updated_by) REFERENCES users (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE
#);
#
#CREATE TABLE IF NOT EXISTS articles
#(
#    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
#    article_id VARCHAR(255) NOT NULL UNIQUE,
#    title      VARCHAR(255) NOT NULL,
#    content    TEXT         NOT NULL,
#    icon       TEXT         NOT NULL,
#    created_by BIGINT       NOT NULL,
#    created_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
#    updated_by BIGINT       NOT NULL,
#    updated_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
#    constraint uq_articles_article_id unique (article_id),
#    CONSTRAINT fk_articles_created_by FOREIGN KEY (created_by) REFERENCES users (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE,
#    CONSTRAINT fk_articles_updated_by FOREIGN KEY (updated_by) REFERENCES users (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE
#);
#
#CREATE TABLE IF NOT EXISTS article_categories
#(
#    article_id BIGINT                                                        NOT NULL,
#    category   ENUM ('LIFESTYLE', 'FINANCES', 'WISDOM', 'FITNESS', 'HEALTH') NOT NULL,
#    PRIMARY KEY (article_id, category),
#    CONSTRAINT fk_article_categories_article_id FOREIGN KEY (article_id) REFERENCES articles (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE
#);
#
#CREATE TABLE IF NOT EXISTS roles
#(
#    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
#    authority    VARCHAR(255),
#    name         VARCHAR(255),
#    reference_id VARCHAR(255),
#    created_by   BIGINT NOT NULL,
#    updated_by   BIGINT NOT NULL,
#    created_at   TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
#    updated_at   TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
#    CONSTRAINT fk_roles_created_by FOREIGN KEY (created_by) REFERENCES users (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE RESTRICT,
#    CONSTRAINT fk_roles_updated_by FOREIGN KEY (updated_by) REFERENCES users (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE RESTRICT
#);
#
#CREATE TABLE IF NOT EXISTS user_roles
#(
#    id      BIGINT AUTO_INCREMENT PRIMARY KEY,
#    user_id BIGINT NOT NULL,
#    role_id BIGINT NOT NULL,
#    CONSTRAINT fk_user_roles_user_id FOREIGN KEY (user_id) REFERENCES users (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE RESTRICT,
#    CONSTRAINT fk_user_roles_role_id FOREIGN KEY (role_id) REFERENCES roles (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE RESTRICT
#);
#
#CREATE INDEX idx_users_email ON users (email);
#CREATE INDEX idx_users_user_id ON users (user_id);
#-- Check if the index exists first
#SELECT COUNT(*)
#FROM information_schema.statistics
#WHERE table_schema = DATABASE()
#  AND table_name = 'users'
#  AND index_name = 'idx_users_email';
#
#-- Create the index only if it doesn't exist
#SET @create_index = IF(@index_count = 0, 'CREATE INDEX idx_users_email ON users (email);', '');
#PREPARE stmt FROM @create_index;
#EXECUTE stmt;
#DEALLOCATE PREPARE stmt;
#
#-- Repeat the same logic for the other index
#SELECT COUNT(*)
#FROM information_schema.statistics
#WHERE table_schema = DATABASE()
#  AND table_name = 'users'
#  AND index_name = 'idx_users_user_id';
#
#SET @create_index = IF(@index_count = 0, 'CREATE INDEX idx_users_user_id ON users (user_id);', '');
#PREPARE stmt FROM @create_index;
#EXECUTE stmt;
#DEALLOCATE PREPARE stmt;

COMMIT;