-- notice_id 컬럼이 없는 경우에만 추가
SET @exist := (SELECT COUNT(*) 
               FROM information_schema.COLUMNS 
               WHERE TABLE_SCHEMA = DATABASE()
                 AND TABLE_NAME = 'post_hashtag' 
                 AND COLUMN_NAME = 'notice_id');

SET @query := IF(@exist = 0, 
    'ALTER TABLE post_hashtag ADD COLUMN notice_id bigint null',
    'SELECT "notice_id column already exists in post_hashtag table" AS message');

PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;