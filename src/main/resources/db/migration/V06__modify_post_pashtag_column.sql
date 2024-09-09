-- notice_id 컬럼이 없는 경우에만 추가
ALTER TABLE post_hashtag ADD COLUMN notice_id bigint NULL;