package sesac.server.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.StringUtils;
import sesac.server.campus.entity.Course;
import sesac.server.user.dto.request.UpdateStudentRequest;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(value = {AuditingEntityListener.class})
public class Student {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false)
    private char gender;

    @Column(nullable = false, unique = true)
    private String nickname;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "first_course_id", nullable = false)
    private Course firstCourse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "second_course_id")
    private Course secondCourse;

    private String profileImage;

    @Column(nullable = false)
    private int statusCode;

    @Column(columnDefinition = "TEXT")
    private String rejectReason;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void update(UpdateStudentRequest request) {
        if (StringUtils.hasText(request.name())) {
            this.name = request.name();
        }

        if (StringUtils.hasText(request.nickname())) {
            this.nickname = request.nickname();
        }
    }

    public void setStatus(Integer statusCode) {
        if (statusCode != null) {
            this.statusCode = statusCode;
        }
    }
}