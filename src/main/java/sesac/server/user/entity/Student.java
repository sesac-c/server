package sesac.server.user.entity;

import static org.springframework.util.StringUtils.hasText;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import sesac.server.campus.entity.Campus;
import sesac.server.campus.entity.Course;
import sesac.server.common.constants.AppConstants;
import sesac.server.common.entity.HasCampus;
import sesac.server.common.exception.BaseException;
import sesac.server.user.dto.request.AcceptStatusRequest;
import sesac.server.user.dto.request.UpdateProfileRequest;
import sesac.server.user.dto.request.UpdateStudentRequest;
import sesac.server.user.exception.UserErrorCode;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(value = {AuditingEntityListener.class})
public class Student implements HasCampus {

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

    @PrePersist
    @PreUpdate
    public void prePersist() {
        if (this.statusCode < 20) {
            this.rejectReason = null;
        }
    }

    public void update(UpdateStudentRequest request) {
        if (hasText(request.name())) {
            this.name = request.name();
        }

        if (hasText(request.nickname())) {
            this.nickname = request.nickname();
        }
    }

    public void setStatus(AcceptStatusRequest request) {
        if (request.statusCode() != null) {
            this.statusCode = request.statusCode();
        }

        if (this.statusCode >= 20 && !hasText(this.rejectReason)
                && !hasText(request.rejectReason())) {
            throw new BaseException(UserErrorCode.REQUIRED_REJECT_REASON);
        }

        if (hasText(request.rejectReason())) {
            this.rejectReason = request.rejectReason();
        }
    }

    @Override
    public Campus getCampus() {
        return this.firstCourse.getCampus();
    }

    public Course getCourse() {
        return this.firstCourse;
    }

    public void updateProfile(UpdateProfileRequest request) {
        if (hasText(request.nickname())) {
            this.nickname = request.nickname();
        }
        if (request.profileImage() == null && request.removed()) {
            this.profileImage = null;
        }

        if (request.profileImage() != null) {
            this.profileImage = request.profileImage();
        }
    }

    public String getProfile() {
        return hasText(this.profileImage) ? this.profileImage
                : AppConstants.DEFAULT_PROFILE_IMAGE;
    }
}