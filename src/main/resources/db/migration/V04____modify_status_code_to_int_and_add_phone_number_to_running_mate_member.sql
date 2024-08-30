ALTER TABLE campus_change_request MODIFY COLUMN status_code INT NOT NULL;               -- type을 varchar에서 int로 변경
ALTER TABLE running_mate_member ADD COLUMN phone_number VARCHAR(20) NOT NULL UNIQUE;    -- phone_number 컬럼 추가
