-- reply 테이블 수정
SET @exist_reply := (SELECT COUNT(*)
                     FROM information_schema.COLUMNS
                     WHERE TABLE_SCHEMA = DATABASE()
                       AND TABLE_NAME = 'reply'
                       AND COLUMN_NAME = 'notice_id');

SET @add_column_reply := IF(@exist_reply = 0,
    'ALTER TABLE reply ADD COLUMN notice_id BIGINT',
    'SELECT "notice_id column already exists in reply table" AS message');

PREPARE stmt FROM @add_column_reply;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- post_id를 nullable로 변경 (이미 nullable이 아닌 경우에만)
SET @is_nullable_reply := (SELECT IS_NULLABLE
                           FROM information_schema.COLUMNS
                           WHERE TABLE_SCHEMA = DATABASE()
                             AND TABLE_NAME = 'reply'
                             AND COLUMN_NAME = 'post_id');

SET @modify_column_reply := IF(@is_nullable_reply = 'NO',
    'ALTER TABLE reply MODIFY COLUMN post_id BIGINT NULL',
    'SELECT "post_id in reply table is already nullable" AS message');

PREPARE stmt FROM @modify_column_reply;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 외래 키 제약 추가 (이미 존재하지 않는 경우에만)
SET @fk_exists_reply := (SELECT COUNT(*)
                         FROM information_schema.TABLE_CONSTRAINTS
                         WHERE TABLE_SCHEMA = DATABASE()
                           AND TABLE_NAME = 'reply'
                           AND CONSTRAINT_NAME = 'fk_reply_notice_id');

SET @add_fk_reply := IF(@fk_exists_reply = 0,
    'ALTER TABLE reply ADD CONSTRAINT fk_reply_notice_id FOREIGN KEY (notice_id) REFERENCES notice(id)',
    'SELECT "Foreign key fk_reply_notice_id already exists in reply table" AS message');

PREPARE stmt FROM @add_fk_reply;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- likes 테이블 수정
SET @exist_likes := (SELECT COUNT(*)
                     FROM information_schema.COLUMNS
                     WHERE TABLE_SCHEMA = DATABASE()
                       AND TABLE_NAME = 'likes'
                       AND COLUMN_NAME = 'notice_id');

SET @add_column_likes := IF(@exist_likes = 0,
    'ALTER TABLE likes ADD COLUMN notice_id BIGINT',
    'SELECT "notice_id column already exists in likes table" AS message');

PREPARE stmt FROM @add_column_likes;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- post_id를 nullable로 변경 (이미 nullable이 아닌 경우에만)
SET @is_nullable_likes := (SELECT IS_NULLABLE
                           FROM information_schema.COLUMNS
                           WHERE TABLE_SCHEMA = DATABASE()
                             AND TABLE_NAME = 'likes'
                             AND COLUMN_NAME = 'post_id');

SET @modify_column_likes := IF(@is_nullable_likes = 'NO',
    'ALTER TABLE likes MODIFY COLUMN post_id BIGINT NULL',
    'SELECT "post_id in likes table is already nullable" AS message');

PREPARE stmt FROM @modify_column_likes;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 외래 키 제약 추가 (이미 존재하지 않는 경우에만)
SET @fk_exists_likes := (SELECT COUNT(*)
                         FROM information_schema.TABLE_CONSTRAINTS
                         WHERE TABLE_SCHEMA = DATABASE()
                           AND TABLE_NAME = 'likes'
                           AND CONSTRAINT_NAME = 'fk_likes_notice_id');

SET @add_fk_likes := IF(@fk_exists_likes = 0,
    'ALTER TABLE likes ADD CONSTRAINT fk_likes_notice_id FOREIGN KEY (notice_id) REFERENCES notice(id)',
    'SELECT "Foreign key fk_likes_notice_id already exists in likes table" AS message');

PREPARE stmt FROM @add_fk_likes;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;