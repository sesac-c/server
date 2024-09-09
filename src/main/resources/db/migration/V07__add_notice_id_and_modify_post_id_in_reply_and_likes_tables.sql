-- reply 테이블 수정
ALTER TABLE reply ADD COLUMN notice_id BIGINT;

-- post_id를 nullable로 변경
ALTER TABLE reply MODIFY COLUMN post_id BIGINT NULL;

-- 외래 키 제약 추가
ALTER TABLE reply ADD CONSTRAINT fk_reply_notice_id FOREIGN KEY (notice_id) REFERENCES notice(id);

-- likes 테이블 수정
ALTER TABLE likes ADD COLUMN notice_id BIGINT;

-- post_id를 nullable로 변경
ALTER TABLE likes MODIFY COLUMN post_id BIGINT NULL;

-- 외래 키 제약 추가
ALTER TABLE likes ADD CONSTRAINT fk_likes_notice_id FOREIGN KEY (notice_id) REFERENCES notice(id);
