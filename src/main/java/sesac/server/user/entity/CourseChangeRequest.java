package sesac.server.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sesac.server.campus.entity.Course;
import sesac.server.common.entity.BaseEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseChangeRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "new_course_id")
    private Course newCourse;

    @Column(name = "status_code", nullable = false)
    private int statusCode;

    @Column(columnDefinition = "TEXT")
    private String rejectReason;

    public void update(int statusCode, String rejectReason) {
        if (statusCode != this.statusCode) {
            this.statusCode = statusCode;
        }
        if (rejectReason != null) {
            this.rejectReason = rejectReason;
        }
    }
}