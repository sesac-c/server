-- 새로운 CourseChangeRequest 테이블 생성
CREATE TABLE course_change_request (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       student_id BIGINT,
                                       new_course_id BIGINT,
                                       status_code INT NOT NULL,
                                       reject_reason TEXT,
                                       created_at TIMESTAMP,
                                       updated_at TIMESTAMP,
                                       FOREIGN KEY (student_id) REFERENCES student(id),
                                       FOREIGN KEY (new_course_id) REFERENCES course(id)
);

-- CampusChangeRequest 데이터를 CourseChangeRequest로 이동
INSERT INTO course_change_request (student_id, new_course_id, status_code, reject_reason, created_at, updated_at)
SELECT
    user_id,
    new_course_id,
    status_code,
    reject_reason,
    created_at,
    updated_at
FROM campus_change_request;

DROP TABLE campus_change_request;