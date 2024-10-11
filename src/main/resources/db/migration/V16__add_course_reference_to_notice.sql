ALTER TABLE notice
    ADD COLUMN course_id BIGINT;
ALTER TABLE notice
    ADD CONSTRAINT fk_notice_course
        FOREIGN KEY (course_id) REFERENCES course (id);