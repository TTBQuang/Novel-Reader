CREATE DATABASE novel_reader_deploy;
USE novel_reader_deploy;

CREATE TABLE users (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255),
    display_name VARCHAR(255) NOT NULL,
    avatar VARCHAR(255),
    cover_image VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_admin BOOLEAN NOT NULL DEFAULT FALSE,
    is_comment_blocked BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE otp_verification (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    otp VARCHAR(10) NOT NULL,
    expiry_date DATETIME NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    username VARCHAR(255),
    password VARCHAR(255),
    otp_type ENUM('REGISTRATION', 'PASSWORD_RESET') NOT NULL,
    attempt_count INT NOT NULL DEFAULT 0,
    CONSTRAINT check_username_password_not_null 
        CHECK (
            (otp_type = 'REGISTRATION' AND username IS NOT NULL AND password IS NOT NULL) 
            OR otp_type = 'PASSWORD_RESET'
        )
);

CREATE TABLE novels (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    poster INT UNSIGNED NOT NULL,
    name VARCHAR(255) NOT NULL,
    author VARCHAR(255) DEFAULT NULL,
    artist VARCHAR(255) DEFAULT NULL,
    cover VARCHAR(255) DEFAULT NULL,
    status ENUM('Đang tiến hành', 'Tạm ngưng', 'Đã hoàn thành') NOT NULL DEFAULT 'Đang tiến hành',
    summary TEXT NOT NULL,
    words_count INT DEFAULT 0 NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_poster FOREIGN KEY (poster) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE chapter_groups (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    novel_id INT UNSIGNED NOT NULL,
    name VARCHAR(255) NOT NULL,
    image VARCHAR(255) NOT NULL,
    group_order DECIMAL NOT NULL,
    CONSTRAINT fk_chapter_groups_novel_id FOREIGN KEY (novel_id) REFERENCES novels(id) ON DELETE CASCADE
);

CREATE TABLE chapters (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    chapter_group_id INT UNSIGNED NOT NULL,
    name VARCHAR(255) NOT NULL,
    content LONGTEXT NOT NULL,
    words_count INT DEFAULT 0 NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    chapter_order DECIMAL NOT NULL,
    CONSTRAINT fk_chapter_group_id FOREIGN KEY (chapter_group_id) REFERENCES chapter_groups(id) ON DELETE CASCADE
);

CREATE TABLE genres (
	id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE novel_genres (
    novel_id INT UNSIGNED,
    genre_id INT UNSIGNED,
    PRIMARY KEY (novel_id, genre_id),
    CONSTRAINT fk_novelgenres_novel_id FOREIGN KEY (novel_id) REFERENCES novels(id) ON DELETE CASCADE,
    CONSTRAINT fk_novelgenres_genre_id FOREIGN KEY (genre_id) REFERENCES genres(id) ON DELETE CASCADE
);

CREATE TABLE comments (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNSIGNED NOT NULL,
    novel_id INT UNSIGNED NOT NULL,
    chapter_id INT UNSIGNED DEFAULT NULL,
    content TEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_comments_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_comments_novel_id FOREIGN KEY (novel_id) REFERENCES novels(id) ON DELETE CASCADE,
    CONSTRAINT fk_comments_chapter_id FOREIGN KEY (chapter_id) REFERENCES chapters(id) ON DELETE CASCADE
);

DELIMITER $$

-- Update column updated_at of a novel when there is new chapter in chapters table
CREATE TRIGGER update_novel_updated_at
AFTER INSERT ON chapters
FOR EACH ROW
BEGIN
    UPDATE novels
    SET updated_at = CURRENT_TIMESTAMP
    WHERE id = (SELECT novel_id FROM chapter_groups WHERE id = NEW.chapter_group_id);
END $$

-- Update words count for novel when a new chapter is inserted
CREATE TRIGGER update_words_count_novel_after_insert
AFTER INSERT ON chapters
FOR EACH ROW
BEGIN
    DECLARE total_words INT;

    SELECT SUM(words_count)
    INTO total_words
    FROM chapters
    WHERE chapter_group_id IN (SELECT id FROM chapter_groups WHERE novel_id = 
        (SELECT novel_id FROM chapter_groups WHERE id = NEW.chapter_group_id));
    
    UPDATE novels
    SET words_count = total_words
    WHERE id = (SELECT novel_id FROM chapter_groups WHERE id = NEW.chapter_group_id);
END $$

-- Set novel_id for comment when set chapter_id for it
CREATE TRIGGER set_novel_id_before_insert_comment
BEFORE INSERT ON comments
FOR EACH ROW
BEGIN
    IF NEW.chapter_id IS NOT NULL THEN
        SET NEW.novel_id = (
            SELECT cg.novel_id
            FROM chapter_groups cg
            JOIN chapters ch ON ch.chapter_group_id = cg.id
            WHERE ch.id = NEW.chapter_id
            LIMIT 1
        );
    END IF;
END $$

CREATE TRIGGER set_default_group_order
BEFORE INSERT ON chapter_groups
FOR EACH ROW
BEGIN
    IF NEW.group_order IS NULL THEN
        SET NEW.group_order = (
            SELECT IFNULL(MAX(group_order), 0) + 1
            FROM chapter_groups
            WHERE novel_id = NEW.novel_id
        );
    END IF;
END $$

CREATE TRIGGER set_default_chapter_order
BEFORE INSERT ON chapters
FOR EACH ROW
BEGIN
    IF NEW.chapter_order IS NULL THEN
        SET NEW.chapter_order = (
            SELECT IFNULL(MAX(chapter_order), 0) + 1
            FROM chapters
            WHERE chapter_group_id = NEW.chapter_group_id
        );
    END IF;
END $$

DELIMITER ;

