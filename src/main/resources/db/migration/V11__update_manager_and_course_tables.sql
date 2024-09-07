-- manager 테이블 변경
-- campus_name 컬럼 삭제
SET @sql = (SELECT IF(
    EXISTS(SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'manager' AND COLUMN_NAME = 'campus_name'),
    'ALTER TABLE manager DROP COLUMN campus_name',
    'SELECT 1'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- address 컬럼 삭제
SET @sql = (SELECT IF(
    EXISTS(SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'manager' AND COLUMN_NAME = 'address'),
    'ALTER TABLE manager DROP COLUMN address',
    'SELECT 1'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- campus_id 컬럼 추가
SET @sql = (SELECT IF(
    NOT EXISTS(SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'manager' AND COLUMN_NAME = 'campus_id'),
    'ALTER TABLE manager ADD COLUMN campus_id BIGINT, ADD CONSTRAINT manager_campus_fk FOREIGN KEY (campus_id) REFERENCES campus(id)',
    'SELECT 1'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- course 테이블 변경
-- 개강날짜 컬럼 추가
SET @sql = (SELECT IF(
    NOT EXISTS(SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'course' AND COLUMN_NAME = 'start_date'),
    'ALTER TABLE course ADD COLUMN start_date DATE',
    'SELECT 1'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 종강날짜 컬럼 추가
SET @sql = (SELECT IF(
    NOT EXISTS(SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'course' AND COLUMN_NAME = 'end_date'),
    'ALTER TABLE course ADD COLUMN end_date DATE',
    'SELECT 1'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 기존 데이터에 대한 임시 날짜 설정 (예: 현재 날짜)
UPDATE course SET start_date = CURDATE(), end_date = CURDATE() WHERE start_date IS NULL OR end_date IS NULL;

-- NOT NULL 제약조건 추가
ALTER TABLE course MODIFY COLUMN start_date DATE NOT NULL;
ALTER TABLE course MODIFY COLUMN end_date DATE NOT NULL;

-- 강의명 + 기수 조합이 유니크해야한다는 제약 조건 추가
SET @constraint_exists = (
    SELECT COUNT(*)
    FROM information_schema.table_constraints
    WHERE constraint_type = 'UNIQUE'
      AND table_schema = 'sesacc'
      AND table_name = 'course'
      AND constraint_name = 'unique_course_class_number'
);

-- 해당 제약 조건이 적용되지 않았을 경우 추가
SET @sql = IF(@constraint_exists = 0,
              'ALTER TABLE course ADD CONSTRAINT unique_course_class_number UNIQUE (name, class_number);',
              'SELECT "Constraint already exists"');

-- 실행
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
