package sesac.server.feed.repository.search;

import static sesac.server.feed.entity.QPost.post;
import static sesac.server.user.entity.QUser.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import sesac.server.feed.dto.PostResponse;
import sesac.server.feed.dto.QPostResponse;

@RequiredArgsConstructor
public class PostSearchImpl implements PostSearch {

    private final JPAQueryFactory queryFactory;

    public List<PostResponse> searchPost(Pageable pageable) {

        List<PostResponse> posts = queryFactory
                .select(new QPostResponse(
                        post.id,
                        user.student.nickname,
                        post.title,
                        post.content,
                        post.createdAt,
                        post.image
                ))
                .from(post)
                .join(post.user, user)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return posts;

        /*JPAQuery<Post> countQuery = queryFactory
                .select(post)
                .from(post);

        return PageableExecutionUtils.getPage(posts, pageable, countQuery::fetchCount);*/
    }
}
