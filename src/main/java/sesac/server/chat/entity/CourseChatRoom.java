package sesac.server.chat.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import sesac.server.campus.entity.Course;

@Entity
@Table(name = "course_chat_room")
@EntityListeners(value = {AuditingEntityListener.class})
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDateTime lastMessageAt;

    private Integer participantCount;

    @Builder.Default
    private boolean active = true;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime deletedAt;

    public void updateLastMessageAt(LocalDateTime timestamp) {
        this.lastMessageAt = timestamp;
    }

    public void deactivate() {
        this.active = false;
        this.deletedAt = LocalDateTime.now();
    }

    // 소프트 삭제 확인
    public boolean isDeleted() {
        return deletedAt != null;
    }
}
