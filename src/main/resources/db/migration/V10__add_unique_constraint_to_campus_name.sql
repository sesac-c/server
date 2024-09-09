-- campus 테이블에 unique 제약 조건 추가
ALTER TABLE campus
    ADD CONSTRAINT unique_name UNIQUE (name);
