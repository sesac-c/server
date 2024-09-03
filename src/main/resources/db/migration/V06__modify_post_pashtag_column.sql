ALTER TABLE post_hashtag
    MODIFY post_id bigint null;

ALTER TABLE post_hashtag
    ADD COLUMN notice_id bigint null;
