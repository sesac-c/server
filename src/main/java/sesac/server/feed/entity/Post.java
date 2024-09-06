package sesac.server.feed.entity;

import static org.springframework.util.StringUtils.hasText;

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
import sesac.server.feed.dto.request.UpdatePostRequest;
import sesac.server.user.entity.User;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post extends BaseEntity {

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

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PostType type;

    @Formula("(SELECT COUNT(*) FROM likes l WHERE l.post_id = id)")
    private Long likesCount;

    @Formula("(SELECT COUNT(*) FROM reply r WHERE r.post_id = id)")
    private Long replyCount;

    @OneToMany(mappedBy = "post")
    @Builder.Default
    private List<PostHashtag> hashtags = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    @Builder.Default
    private List<Reply> replies = new ArrayList<>();

    public void update(UpdatePostRequest request) {

        if (hasText(request.title())) {
            title = request.title();
        }

        if (hasText(request.content())) {
            content = request.content();
        }

    }
}