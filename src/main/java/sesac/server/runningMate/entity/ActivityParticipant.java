package sesac.server.runningMate.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_report_id")
    private ActivityReport activityReport;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "running_mate_member_id")
    private RunningMateMember runningMateMember;
}