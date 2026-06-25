SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS `comment`;
DROP TABLE IF EXISTS `book`;
DROP TABLE IF EXISTS `review`;
DROP TABLE IF EXISTS `review_like`;
DROP TABLE IF EXISTS `book_status`;
DROP TABLE IF EXISTS `book_recommendation`;
DROP TABLE IF EXISTS `notification`;
DROP TABLE IF EXISTS `user`;
DROP TABLE IF EXISTS `book_category`;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE `user` (
    id              BINARY(16)      NOT NULL,
    email           VARCHAR(320)    NOT NULL,
    nickname        VARCHAR(20)     NOT NULL,
    password        VARCHAR(255)    NOT NULL,
    deleted_at      DATETIME(6)     NULL,
    created_at      DATETIME(6)     NOT NULL,
    updated_at      DATETIME(6)     NOT NULL
);
ALTER TABLE `user` ADD CONSTRAINT pk_user_id PRIMARY KEY (id);
ALTER TABLE `user` ADD CONSTRAINT uk_user_email UNIQUE (email);

CREATE TABLE book_category (
    id              BINARY(16)      NOT NULL,
    name            VARCHAR(100)    NOT NULL,
    parent_id       BINARY(16)      NULL,
    depth           INTEGER         NOT NULL,
    path            VARCHAR(255)    NOT NULL,
    created_at      DATETIME(6)     NOT NULL,
    updated_at      DATETIME(6)     NOT NULL
);
ALTER TABLE book_category ADD CONSTRAINT pk_book_category_id PRIMARY KEY (id);
ALTER TABLE book_category ADD CONSTRAINT fk_book_category_parent_id FOREIGN KEY (parent_id) REFERENCES book_category(id);

CREATE TABLE book (
    id              BINARY(16)      NOT NULL,
    title           VARCHAR(255)    NOT NULL,
    author          VARCHAR(50)     NOT NULL,
    description     VARCHAR(1000)   NULL,
    publisher       VARCHAR(50)     NOT NULL,
    published_date  DATE            NULL,
    isbn            VARCHAR(20)     NOT NULL UNIQUE,
    thumbnail_url   VARCHAR(100)    NULL,
    review_count    BIGINT          NULL DEFAULT 0,
    rating          DOUBLE          NULL DEFAULT 0,
    book_category_id
                    BINARY(16)      NOT NULL,
    created_at      DATETIME(6)     NOT NULL,
    updated_at      DATETIME(6)     NOT NULL,
    deleted_at      DATETIME(6)     NULL
);
ALTER TABLE book ADD CONSTRAINT pk_book_id PRIMARY KEY (id);
ALTER TABLE book ADD CONSTRAINT uk_book_isbn UNIQUE (isbn);
ALTER TABLE book ADD CONSTRAINT fk_book_book_category FOREIGN KEY (book_category_id) REFERENCES book_category(id);

CREATE TABLE review (
    id              BINARY(16)      NOT NULL,
    book_id         BINARY(16)      NOT NULL,
    user_id         BINARY(16)      NOT NULL,
    content         VARCHAR(1000)   NOT NULL,
    attachment_url  VARCHAR(100)    NULL,
    rating          INTEGER         NOT NULL CHECK (rating BETWEEN 1 AND 5),
    like_count      BIGINT          NOT NULL DEFAULT 0,
    comment_count   BIGINT          NOT NULL DEFAULT 0,
    created_at      DATETIME(6)     NOT NULL,
    updated_at      DATETIME(6)     NOT NULL,
    deleted_at      DATETIME(6)     NULL
);
ALTER TABLE review ADD CONSTRAINT pk_review_id PRIMARY KEY (id);
ALTER TABLE review ADD CONSTRAINT uk_review_book_user UNIQUE (book_id, user_id);
ALTER TABLE review ADD CONSTRAINT fk_review_book FOREIGN KEY (book_id) REFERENCES book(id);
ALTER TABLE review ADD CONSTRAINT fk_review_user FOREIGN KEY (user_id) REFERENCES `user`(id);

CREATE TABLE review_like (
    id              BINARY(16) NOT NULL,
    review_id       BINARY(16) NOT NULL,
    user_id         BINARY(16) NOT NULL,
    created_at      DATETIME(6) NOT NULL,
    updated_at      DATETIME(6) NOT NULL
);
ALTER TABLE review_like ADD CONSTRAINT pk_review_like_id PRIMARY KEY (id);
ALTER TABLE review_like ADD CONSTRAINT fk_review_like_review FOREIGN KEY (review_id) REFERENCES review (id);
ALTER TABLE review_like ADD CONSTRAINT fk_review_like_user FOREIGN KEY (user_id) REFERENCES `user` (id);
ALTER TABLE review_like ADD CONSTRAINT uk_review_like_review_user UNIQUE (review_id, user_id);

CREATE TABLE comment (
    id              BINARY(16)       NOT NULL,
    user_id         BINARY(16)       NOT NULL,
    review_id       BINARY(16)       NOT NULL,
    content         VARCHAR(500)     NOT NULL,
    deleted_at      DATETIME(6)      NULL,
    created_at      DATETIME(6)      NOT NULL,
    updated_at      DATETIME(6)      NOT NULL
);
ALTER TABLE comment ADD CONSTRAINT pk_comment_id PRIMARY KEY (id);
ALTER TABLE comment ADD CONSTRAINT uk_comment_user_review UNIQUE (user_id, review_id);
ALTER TABLE comment ADD CONSTRAINT fk_comment_user FOREIGN KEY (user_id) REFERENCES `user`(id);
ALTER TABLE comment ADD CONSTRAINT fk_comment_review FOREIGN KEY (review_id) REFERENCES review(id);

CREATE TABLE book_status (
    id              BINARY(16)      NOT NULL,
    user_id         BINARY(16)      NOT NULL,
    book_id         BINARY(16)      NOT NULL,
    status          VARCHAR(10)     NOT NULL,
    created_at      DATETIME(6)     NOT NULL,
    updated_at      DATETIME(6)     NOT NULL
);
ALTER TABLE book_status ADD CONSTRAINT pk_book_status_id PRIMARY KEY (id);
ALTER TABLE book_status ADD CONSTRAINT uk_book_status_user_book UNIQUE (user_id, book_id);
ALTER TABLE book_status ADD CONSTRAINT fk_book_status_user FOREIGN KEY (user_id) REFERENCES `user`(id);
ALTER TABLE book_status ADD CONSTRAINT fk_book_status_book FOREIGN KEY (book_id) REFERENCES book(id);

CREATE TABLE book_recommendation (
    id              BINARY(16)      NOT NULL,
    user_id         BINARY(16)      NOT NULL,
    book_id         BINARY(16)      NOT NULL,
    created_at      DATETIME(6)     NOT NULL,
    updated_at      DATETIME(6)     NOT NULL
);
ALTER TABLE book_recommendation ADD CONSTRAINT pk_book_recommendation_id PRIMARY KEY (id);
ALTER TABLE book_recommendation ADD CONSTRAINT uk_book_recommendation_user_book UNIQUE (user_id, book_id);
ALTER TABLE book_recommendation ADD CONSTRAINT fk_book_recommendation_user FOREIGN KEY (user_id) REFERENCES `user`(id);
ALTER TABLE book_recommendation ADD CONSTRAINT fk_book_recommendation_book FOREIGN KEY (book_id) REFERENCES  book(id);

CREATE TABLE notification
(
    id              BINARY(16)    NOT NULL,
    user_id         BINARY(16)    NOT NULL,
    review_id       BINARY(16)    NOT NULL,
    message         VARCHAR(1000) NULL,
    confirmed       BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at      DATETIME(6)   NOT NULL,
    updated_at      DATETIME(6)   NOT NULL
);
ALTER TABLE notification ADD CONSTRAINT pk_notification_id PRIMARY KEY (id);
ALTER TABLE notification ADD CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES `user`(id);
ALTER TABLE notification ADD CONSTRAINT fk_notifications_review FOREIGN KEY (review_id) REFERENCES review(id);