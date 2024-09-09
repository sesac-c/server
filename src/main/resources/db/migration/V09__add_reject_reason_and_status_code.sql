-- 1. student 테이블에 reject_reason 컬럼 추가
ALTER TABLE student ADD COLUMN reject_reason TEXT NULL;

-- 2. campus_change_request 테이블에 reject_reason 컬럼 추가
ALTER TABLE campus_change_request ADD COLUMN reject_reason TEXT NULL;

-- 3. activity_report 테이블에 status_code 컬럼 추가
ALTER TABLE activity_report ADD COLUMN status_code INT NOT NULL;

-- 4. activity_report 테이블에 reject_reason 컬럼 추가
ALTER TABLE activity_report ADD COLUMN reject_reason TEXT NULL;
