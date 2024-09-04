package sesac.server.feed.entity;

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
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;
import sesac.server.common.entity.BaseEntity;
import sesac.server.user.entity.User;


@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    private String image;

    private String thumbnail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NoticeType type;

    private Integer importance;

    private Boolean status;

    @Formula("(SELECT COUNT(*) FROM likes l WHERE l.notice_id = id AND l.type = 'NOTICE')")
    private Long likesCount;

    @Formula("(SELECT COUNT(*) FROM reply r WHERE r.notice_id = id AND r.type = 'NOTICE')")
    private Long replyCount;

    @OneToMany(mappedBy = "notice")
    @Builder.Default
    private List<Reply> replies = new ArrayList<>();

    @OneToMany(mappedBy = "notice")
    @Builder.Default
    private List<Likes> likes = new ArrayList<>();
}
