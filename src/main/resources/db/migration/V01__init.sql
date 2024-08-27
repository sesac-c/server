CREATE TABLE user
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,                              -- 사용자 고유 번호
    type       VARCHAR(20)  NOT NULL,                                          -- 사용자 유형 enum 'manager', 'student', 'graduate'
    email      VARCHAR(255) NOT NULL UNIQUE,                                   -- 이메일
    password   VARCHAR(255) NOT NULL,                                          -- 비밀번호
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,                            -- 생성일시
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP -- 수정일시
);

CREATE TABLE manager
(
    id            BIGINT PRIMARY KEY,                                              -- 사용자 고유 번호
    campus_name   VARCHAR(255) NOT NULL,                                           -- 캠퍼스 이름
    address       VARCHAR(255) NOT NULL,                                           -- 캠퍼스 주소
    profile_image VARCHAR(255),                                                    -- 프로필 이미지
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 수정일시
    FOREIGN KEY (id) REFERENCES user (id) ON DELETE CASCADE                        -- user 테이블과 외래 키 관계
);

CREATE TABLE student
(
    id               BIGINT PRIMARY KEY,                                             -- 사용자 고유 번호
    name             VARCHAR(255) NOT NULL,                                          -- 이름
    birth_date       DATE         NOT NULL,                                          -- 생년월일
    gender           CHAR(1)      NOT NULL,                                          -- 성별
    nickname         VARCHAR(10)  NOT NULL UNIQUE,                                   -- 닉네임
    first_course_id  BIGINT       NOT NULL,                                          -- 첫 번째 과정 ID
    second_course_id BIGINT,                                                         -- 두 번째 과정 ID
    status_code      INT          NOT NULL,                                          -- 상태 코드
    profile_image    VARCHAR(255),                                                   -- 프로필 이미지
    updated_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,-- 수정일시
    FOREIGN KEY (id) REFERENCES user (id) ON DELETE CASCADE                          -- user 테이블과 외래 키 관계
);

CREATE TABLE campus
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,                              -- 캠퍼스 고유 번호
    name       VARCHAR(255) NOT NULL,                                          -- 캠퍼스 이름
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,                            -- 생성일시
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP -- 수정일시
);

CREATE TABLE course
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,                               -- 과정 고유 번호
    name            VARCHAR(255) NOT NULL,                                           -- 과정 이름
    class_number    VARCHAR(255) NOT NULL,                                           -- 클래스 번호
    instructor_name VARCHAR(255) NOT NULL,                                           -- 강사 이름
    campus_id       BIGINT,                                                          -- 캠퍼스 ID
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,                             -- 생성일시
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 수정일시
    FOREIGN KEY (campus_id) REFERENCES campus (id) ON DELETE SET NULL                -- campus 테이블과 외래 키 관계
);

CREATE TABLE post
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,                               -- 게시글 고유 번호
    user_id    BIGINT,                                                          -- 사용자 ID
    title      VARCHAR(255) NOT NULL,                                           -- 제목
    content    TEXT,                                                            -- 내용
    image      VARCHAR(255),                                                    -- 이미지
    thumbnail  VARCHAR(255),                                                    -- 썸네일
    type       VARCHAR(20)  NOT NULL,                                           -- 게시글 유형 enum 'campus', 'all'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,                             -- 생성일시
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 수정일시
    FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE SET NULL               -- user 테이블과 외래 키 관계
);

CREATE TABLE notice
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,                               -- 공지 고유 번호
    user_id    BIGINT       NOT NULL,                                           -- 사용자 ID
    title      VARCHAR(255) NOT NULL,                                           -- 제목
    content    TEXT         NOT NULL,                                           -- 내용
    image      VARCHAR(255),                                                    -- 이미지
    thumbnail  VARCHAR(255),                                                    -- 썸네일
    type       VARCHAR(20)  NOT NULL,                                           -- 공지 유형 enum ('campus', 'all', 'group')
    importance INT,                                                             -- 중요도
    status     BOOLEAN   DEFAULT FALSE,                                         -- 공지 노출 상태
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,                             -- 생성일시
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 수정일시
    FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE                -- user 테이블과 외래 키 관계
);

CREATE TABLE reply
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,                               -- 댓글 고유 번호
    user_id    BIGINT      NOT NULL,                                            -- 사용자 ID
    post_id    BIGINT      NOT NULL,                                            -- 게시글 ID
    content    TEXT        NOT NULL,                                            -- 댓글 내용
    type       VARCHAR(20) NOT NULL,                                            -- ENUM post, notice
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,                             -- 생성일시
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 수정일시
    FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE,               -- user 테이블과 외래 키 관계
    FOREIGN KEY (post_id) REFERENCES post (id) ON DELETE CASCADE                -- post 테이블과 외래 키 관계
);

CREATE TABLE likes
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,                 -- 좋아요 고유 번호
    user_id    BIGINT      NOT NULL,                              -- 사용자 ID
    post_id    BIGINT      NOT NULL,                              -- 게시글 ID
    type       VARCHAR(20) NOT NULL,                              -- ENUM post, notice
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,               -- 생성일시
    FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE, -- user 테이블과 외래 키 관계
    FOREIGN KEY (post_id) REFERENCES post (id) ON DELETE CASCADE  -- post 테이블과 외래 키 관계
);

CREATE TABLE follow
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,                   -- 팔로우 고유 번호
    follower_id  BIGINT NOT NULL,                                     -- 팔로워 ID
    following_id BIGINT NOT NULL,                                     -- 팔로잉 ID
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,                 -- 생성일시
    FOREIGN KEY (follower_id) REFERENCES user (id) ON DELETE CASCADE, -- user 테이블과 외래 키 관계
    FOREIGN KEY (following_id) REFERENCES user (id) ON DELETE CASCADE -- user 테이블과 외래 키 관계
);

CREATE TABLE hashtag
(
    id   BIGINT AUTO_INCREMENT PRIMARY KEY, -- 해시태그 고유 번호
    type VARCHAR(20)         NOT NULL,      -- ENUM post, notice
    name VARCHAR(255) UNIQUE NOT NULL       -- 해시태그 이름
);

CREATE TABLE post_hashtag
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,                      -- 게시글 해시태그 고유 번호
    post_id    BIGINT NOT NULL,                                        -- 게시글 ID
    hashtag_id BIGINT NOT NULL,                                        -- 해시태그 ID
    FOREIGN KEY (post_id) REFERENCES post (id) ON DELETE CASCADE,      -- post 테이블과 외래 키 관계
    FOREIGN KEY (hashtag_id) REFERENCES hashtag (id) ON DELETE CASCADE -- hashtag 테이블과 외래 키 관계
);

CREATE TABLE notification
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,                    -- 알림 고유 번호
    user_id    BIGINT      NOT NULL,                                 -- 사용자 ID
    sender_id  BIGINT      NOT NULL,                                 -- 발신자 ID
    type       VARCHAR(20) NOT NULL,                                 -- 알림 유형 ENUM('like', 'reply', 'follow')
    post_id    BIGINT,                                               -- 게시글 ID
    is_read    BOOLEAN   DEFAULT FALSE,                              -- 읽음 상태
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,                  -- 생성일시
    FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE,    -- user 테이블과 외래 키 관계
    FOREIGN KEY (sender_id) REFERENCES user (id) ON DELETE RESTRICT, -- user 테이블과 외래 키 관계
    FOREIGN KEY (post_id) REFERENCES post (id) ON DELETE RESTRICT    -- post 테이블과 외래 키 관계
);

CREATE TABLE message
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,                    -- 메시지 고유 번호
    sender_id   BIGINT,                                               -- 발신자 ID
    receiver_id BIGINT,                                               -- 수신자 ID
    content     TEXT NOT NULL,                                        -- 메시지 내용
    is_read     BOOLEAN   DEFAULT FALSE,                              -- 읽음 상태
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,                  -- 생성일시
    FOREIGN KEY (sender_id) REFERENCES user (id) ON DELETE SET NULL,  -- user 테이블과 외래 키 관계
    FOREIGN KEY (receiver_id) REFERENCES user (id) ON DELETE SET NULL -- user 테이블과 외래 키 관계
);

CREATE TABLE group_chat
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,                 -- 그룹 채팅 고유 번호
    group_id   BIGINT,                                            -- 그룹 ID
    user_id    BIGINT,                                            -- 사용자 ID
    content    TEXT NOT NULL,                                     -- 채팅 내용
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,               -- 생성일시
    FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE SET NULL -- user 테이블과 외래 키 관계
);

CREATE TABLE running_mate
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,                               -- 러닝메이트 고유 번호
    name       VARCHAR(255) NOT NULL,                                           -- 이름
    subject    VARCHAR(100) NOT NULL,                                           -- 주제
    goal       VARCHAR(100) NOT NULL,                                           -- 목표
    course_id  BIGINT,                                                          -- 과정 ID
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,                             -- 생성일시
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 수정일시
    FOREIGN KEY (course_id) REFERENCES course (id) ON DELETE SET NULL           -- course 테이블과 외래 키 관계
);

CREATE TABLE running_mate_member
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,                            -- 러닝메이트 멤버 고유 번호
    running_mate_id BIGINT      NOT NULL,                                         -- 러닝메이트 ID
    user_id         BIGINT      NOT NULL,                                         -- 사용자 ID
    role            VARCHAR(20) NOT NULL,                                         -- 역할 ENUM('leader', 'member')
    FOREIGN KEY (running_mate_id) REFERENCES running_mate (id) ON DELETE CASCADE, -- running_mate 테이블과 외래 키 관계
    FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE                  -- user 테이블과 외래 키 관계
);

CREATE TABLE activity_report
(
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,           -- 활동 보고서 고유 번호
    activity_duration   INT,                                         -- 활동 시간
    main_content        VARCHAR(100),                                -- 주요 내용
    achievement_summary VARCHAR(20),                                 -- 성과 요약
    photo               VARCHAR(255),                                -- 사진
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP -- 생성일시
);

CREATE TABLE activity_participant
(
    id                     BIGINT AUTO_INCREMENT PRIMARY KEY,                                  -- 활동 참가자 고유 번호
    activity_report_id     BIGINT,                                                             -- 활동 보고서 ID
    running_mate_member_id BIGINT,                                                             -- 러닝메이트 멤버 ID
    FOREIGN KEY (activity_report_id) REFERENCES activity_report (id) ON DELETE CASCADE,        -- activity_report 테이블과 외래 키 관계
    FOREIGN KEY (running_mate_member_id) REFERENCES running_mate_member (id) ON DELETE CASCADE -- running_mate_member 테이블과 외래 키 관계
);

CREATE TABLE restaurant
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,                    -- 식당 고유 번호
    name       VARCHAR(255) NOT NULL,                                -- 식당 이름
    address_id VARCHAR(50),                                          -- 카카오맵 API ID
    address    TEXT,                                                 -- 도로명 주소
    longitude  DECIMAL,                                              -- 위도
    latitude   DECIMAL,                                              -- 경도
    category   VARCHAR(255),                                         -- 카테고리
    campus_id  BIGINT       NOT NULL,                                -- 캠퍼스 ID
    type       VARCHAR(20)  NOT NULL,                                -- 식당 유형 ENUM('course', 'running_mate')
    FOREIGN KEY (campus_id) REFERENCES campus (id) ON DELETE CASCADE -- campus 테이블과 외래 키 관계
);

CREATE TABLE menu
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,                          -- 메뉴 고유 번호
    restaurant_id BIGINT,                                                     -- 식당 ID
    name          VARCHAR(255) NOT NULL,                                      -- 메뉴 이름
    price         DECIMAL,                                                    -- 가격
    FOREIGN KEY (restaurant_id) REFERENCES restaurant (id) ON DELETE SET NULL -- restaurant 테이블과 외래 키 관계
);

CREATE TABLE campus_change_request
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,                               -- 캠퍼스 변경 요청 고유 번호
    user_id       BIGINT,                                                          -- 사용자 ID
    new_course_id BIGINT,                                                          -- 새 과정 ID
    status_code   VARCHAR(20) NOT NULL,                                            -- 상태 코드  ENUM('pending', 'approved', 'rejected')
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,                             -- 생성일시
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 수정일시
    FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE SET NULL,                 -- user 테이블과 외래 키 관계
    FOREIGN KEY (new_course_id) REFERENCES course (id) ON DELETE SET NULL          -- course 테이블과 외래 키 관계
);

-- (Redis)
CREATE TABLE password_reset
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,                 -- 비밀번호 재설정 고유 번호
    user_id    BIGINT,                                            -- 사용자 ID
    token      VARCHAR(255) NOT NULL,                             -- 토큰
    expires_at TIMESTAMP,                                         -- 만료일시
    FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE SET NULL -- user 테이블과 외래 키 관계
);

-- (Redis)
CREATE TABLE email_validate
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,                 -- 이메일 검증 고유 번호
    user_id    BIGINT,                                            -- 사용자 ID
    token      VARCHAR(255) NOT NULL,                             -- 토큰
    expires_at TIMESTAMP,                                         -- 만료일시
    FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE SET NULL -- user 테이블과 외래 키 관계
);