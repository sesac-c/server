-- running_mate_id 컬럼 추가 및 외래 키 설정
ALTER TABLE activity_report
    ADD COLUMN running_mate_id BIGINT,
    ADD CONSTRAINT activity_report_fk FOREIGN KEY (running_mate_id) REFERENCES running_mate (id);
