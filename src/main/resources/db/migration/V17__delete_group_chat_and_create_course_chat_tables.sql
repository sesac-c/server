-- group_chat 테이블 삭제
DROP TABLE IF EXISTS group_chat;

-- course_chat_room 테이블 생성
CREATE TABLE IF NOT EXISTS course_chat_room (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    last_message_at DATETIME NOT NULL,
    participant_count INT DEFAULT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    deleted_at DATETIME DEFAULT NULL,
    CONSTRAINT fk_course_chat_room_course FOREIGN KEY (course_id) REFERENCES course(id)
);

-- course_chat_messages 테이블 생성
CREATE TABLE IF NOT EXISTS course_chat_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_chat_room_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    delivered BOOLEAN NOT NULL,
    CONSTRAINT fk_course_chat_message_room FOREIGN KEY (course_chat_room_id) REFERENCES course_chat_room(id) ON DELETE CASCADE,
    CONSTRAINT fk_course_chat_message_sender FOREIGN KEY (sender_id) REFERENCES student(id)
);

-- course_chat_room_user_status 테이블 생성
CREATE TABLE IF NOT EXISTS course_chat_room_user_status (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_chat_room_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    last_read_message_id BIGINT DEFAULT NULL,
    last_read_at DATETIME NOT NULL,
    CONSTRAINT fk_course_chat_room_status_room FOREIGN KEY (course_chat_room_id) REFERENCES course_chat_room(id) ON DELETE CASCADE,
    CONSTRAINT fk_course_chat_room_status_student FOREIGN KEY (student_id) REFERENCES student(id)
);

-- 읽음 상태 조회 최적화를 위한 인덱스
CREATE INDEX idx_course_chat_room_user_status_lookup
    ON course_chat_room_user_status (course_chat_room_id, student_id);

-- 메시지 조회 최적화를 위한 인덱스
CREATE INDEX idx_course_chat_messages_room_created
    ON course_chat_messages (course_chat_room_id, created_at DESC);