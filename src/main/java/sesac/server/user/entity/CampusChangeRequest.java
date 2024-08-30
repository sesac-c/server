package sesac.server.user.entity;

import jakarta.persistence.*;
import lombok.*;
import sesac.server.campus.entity.Course;
import sesac.server.common.entity.BaseEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampusChangeRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "new_course_id")
    private Course newCourse;

    @Column(name = "status_code", nullable = false)
    private int statusCode;
}