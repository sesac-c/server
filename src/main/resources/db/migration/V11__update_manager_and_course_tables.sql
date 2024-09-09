-- manager 테이블 변경
-- campus_name 컬럼 삭제
ALTER TABLE manager DROP COLUMN campus_name;

-- address 컬럼 삭제
ALTER TABLE manager DROP COLUMN address;

-- campus_id 컬럼 추가 및 외래 키 설정
ALTER TABLE manager ADD COLUMN campus_id BIGINT,
ADD CONSTRAINT manager_campus_fk FOREIGN KEY (campus_id) REFERENCES campus(id);

-- course 테이블 변경
-- 개강날짜 컬럼 추가
ALTER TABLE course ADD COLUMN start_date DATE;

-- 종강날짜 컬럼 추가
ALTER TABLE course ADD COLUMN end_date DATE;

-- NOT NULL 제약 조건 추가
ALTER TABLE course MODIFY COLUMN start_date DATE NOT NULL;
ALTER TABLE course MODIFY COLUMN end_date DATE NOT NULL;

-- 강의명 + 기수 조합이 유니크해야 한다는 제약 조건 추가
ALTER TABLE course ADD CONSTRAINT unique_course_class_number UNIQUE (name, class_number);
