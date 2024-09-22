package sesac.server.group.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import sesac.server.group.dto.request.UpdateRunningMateMemberRequest;
import sesac.server.user.entity.User;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RunningMateMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "running_mate_id", nullable = false)
    private RunningMate runningMate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole role;

    @Column(name = "phone_number", nullable = false, unique = true, length = 20)
    private String phoneNumber;

    public enum MemberRole {
        LEADER, MEMBER
    }

    public void update(UpdateRunningMateMemberRequest request) {
        setRole(request.role());

        if (request.phoneNumber() != null) {
            this.phoneNumber = request.phoneNumber();
        }
    }

    public void setRole(MemberRole role) {
        if (role != null) {
            this.role = role;
        }
    }
}