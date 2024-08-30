package sesac.server.runningMate.entity;
import jakarta.persistence.*;
import lombok.*;
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
}