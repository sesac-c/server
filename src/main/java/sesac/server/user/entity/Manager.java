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
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import sesac.server.campus.entity.Campus;
import sesac.server.common.constants.AppConstants;
import sesac.server.common.entity.HasCampus;
import sesac.server.user.dto.request.UpdateProfileRequest;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(value = {AuditingEntityListener.class})
public class Manager implements HasCampus {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campus_id")
    private Campus campus;

    private String profileImage;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void updateProfile(UpdateProfileRequest request) {
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
