package sesac.server.campus.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sesac.server.common.entity.BaseEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Course extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String classNumber;

    private String instructorName;

    @ManyToOne
    @JoinColumn(name = "campus_id")
    private Campus campus;
}
