-- reply 테이블에서 type 컬럼 삭제
ALTER TABLE reply DROP COLUMN type;

-- likes 테이블에서 type 컬럼 삭제
ALTER TABLE likes DROP COLUMN type;

-- post_hashtag 테이블에서 type 컬럼 삭제
ALTER TABLE post_hashtag DROP COLUMN type;
