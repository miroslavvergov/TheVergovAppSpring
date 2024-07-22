START TRANSACTION;

CREATE TABLE IF NOT EXISTS test
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS users
(
    id                  BIGSERIAL PRIMARY KEY,
    user_id             VARCHAR(255) NOT NULL,
    first_name          VARCHAR(255),
    username            VARCHAR(255),
    last_name           VARCHAR(255),
    email               VARCHAR(255),
    bio                 VARCHAR(255),
    reference_id        VARCHAR(255),
    qr_code_secret      VARCHAR(255),
    qr_code_image_uri   TEXT,
    image_url           VARCHAR(255),
    last_login          TIMESTAMP(6)          DEFAULT CURRENT_TIMESTAMP,
    login_attempts      INT                   DEFAULT 0,
    mfa                 BOOLEAN      NOT NULL DEFAULT FALSE,
    enabled             BOOLEAN      NOT NULL DEFAULT FALSE,
    account_non_expired BOOLEAN      NOT NULL DEFAULT FALSE,
    account_non_locked  BOOLEAN      NOT NULL DEFAULT FALSE,
    created_by          BIGINT       NOT NULL,
    updated_by          BIGINT       NOT NULL,
    created_at          TIMESTAMP(6)          DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP(6)          DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_users_email UNIQUE (email),
    CONSTRAINT uq_users_user_id UNIQUE (user_id),
    CONSTRAINT fk_users_created_by FOREIGN KEY (created_by) REFERENCES users (id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_users_updated_by FOREIGN KEY (updated_by) REFERENCES users (id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS confirmations
(
    id           BIGSERIAL PRIMARY KEY,
    token_key      VARCHAR(255) UNIQUE,
    user_id      BIGINT NOT NULL UNIQUE,
    reference_id VARCHAR(255),
    created_by   BIGINT NOT NULL,
    updated_by   BIGINT NOT NULL,
    created_at   TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_confirmations_user_id UNIQUE (user_id),
    CONSTRAINT uq_confirmations_key UNIQUE (token_key),
    CONSTRAINT fk_confirmations_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_confirmations_created_by FOREIGN KEY (created_by) REFERENCES users (id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_confirmations_updated_by FOREIGN KEY (updated_by) REFERENCES users (id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS credentials
(
    id           BIGSERIAL PRIMARY KEY,
    password     VARCHAR(255) NOT NULL,
    reference_id VARCHAR(255),
    user_id      BIGINT       NOT NULL UNIQUE,
    created_by   BIGINT       NOT NULL,
    updated_by   BIGINT       NOT NULL,
    created_at   TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_credentials_user_id UNIQUE (user_id),
    CONSTRAINT fk_credentials_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_credentials_created_by FOREIGN KEY (created_by) REFERENCES users (id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_credentials_updated_by FOREIGN KEY (updated_by) REFERENCES users (id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS articles
(
    id         BIGSERIAL PRIMARY KEY,
    article_id VARCHAR(255) NOT NULL UNIQUE,
    title      VARCHAR(255) NOT NULL,
    content    TEXT         NOT NULL,
    icon       TEXT         NOT NULL,
    created_by BIGINT       NOT NULL,
    created_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT       NOT NULL,
    updated_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_articles_article_id UNIQUE (article_id),
    CONSTRAINT fk_articles_created_by FOREIGN KEY (created_by) REFERENCES users (id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_articles_updated_by FOREIGN KEY (updated_by) REFERENCES users (id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS papers
(
    id         BIGSERIAL PRIMARY KEY,
    paper_id   VARCHAR(255) NOT NULL UNIQUE,
    title      VARCHAR(255) NOT NULL,
    content    TEXT         NOT NULL,
    icon       TEXT         NOT NULL,
    created_by BIGINT       NOT NULL,
    created_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT       NOT NULL,
    updated_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_papers_paper_id UNIQUE (paper_id),
    CONSTRAINT fk_papers_created_by FOREIGN KEY (created_by) REFERENCES users (id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_papers_updated_by FOREIGN KEY (updated_by) REFERENCES users (id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TYPE article_category AS ENUM ('LIFESTYLE', 'FINANCES', 'WISDOM', 'FITNESS', 'HEALTH');

CREATE TABLE IF NOT EXISTS article_categories
(
    article_id BIGINT            NOT NULL,
    category   article_category  NOT NULL,
    PRIMARY KEY (article_id, category),
    CONSTRAINT fk_article_categories_article_id FOREIGN KEY (article_id) REFERENCES articles (id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS roles
(
    id           BIGSERIAL PRIMARY KEY,
    authority    VARCHAR(255),
    name         VARCHAR(255),
    reference_id VARCHAR(255),
    created_by   BIGINT NOT NULL,
    updated_by   BIGINT NOT NULL,
    created_at   TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_roles_created_by FOREIGN KEY (created_by) REFERENCES users (id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_roles_updated_by FOREIGN KEY (updated_by) REFERENCES users (id) ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS user_roles
(
    id      BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    CONSTRAINT fk_user_roles_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_user_roles_role_id FOREIGN KEY (role_id) REFERENCES roles (id) ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE INDEX index_users_email ON users (email);
CREATE INDEX index_users_user_id ON users (user_id);
CREATE INDEX index_confirmations_user_id ON confirmations (user_id);
CREATE INDEX index_credentials_user_id ON credentials (user_id);
CREATE INDEX index_user_roles_user_id ON user_roles (user_id);

COMMIT;
