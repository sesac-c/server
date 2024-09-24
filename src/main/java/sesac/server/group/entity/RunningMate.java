package sesac.server.group.entity;

import static org.springframework.util.StringUtils.hasText;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sesac.server.campus.entity.Course;
import sesac.server.common.entity.BaseEntity;
import sesac.server.group.dto.request.UpdateRunningMateRequest;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RunningMate extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 100)
    private String subject;

    @Column(nullable = false, length = 100)
    private String goal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @OneToMany(mappedBy = "runningMate")
    private List<RunningMateMember> members = new ArrayList<>();

    public void update(UpdateRunningMateRequest request) {
        if (hasText(request.name())) {
            this.name = request.name();
        }

        if (hasText(request.subject())) {
            this.subject = request.subject();
        }

        if (hasText(request.goal())) {
            this.goal = request.goal();
        }
    }
}