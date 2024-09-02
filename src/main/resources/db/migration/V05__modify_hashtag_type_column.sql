ALTER TABLE post_hashtag
    ADD COLUMN type VARCHAR(20);
ALTER TABLE hashtag
    DROP COLUMN type;
