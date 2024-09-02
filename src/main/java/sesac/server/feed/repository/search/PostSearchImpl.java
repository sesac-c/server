package sesac.server.feed.repository.search;

import static sesac.server.feed.entity.QPost.post;
import static sesac.server.user.entity.QUser.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import sesac.server.feed.dto.PostListResponse;
import sesac.server.feed.entity.Post;

@RequiredArgsConstructor
public class PostSearchImpl implements PostSearch {

    private final JPAQueryFactory queryFactory;

    public List<PostListResponse> searchPost(Pageable pageable) {

        List<Post> postList = queryFactory
                .selectFrom(post)
                .join(post.user, user)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<PostListResponse> posts = postList.stream().map(PostListResponse::new).toList();

        return posts;
//        return null;
        /*JPAQuery<Post> countQuery = queryFactory
                .select(post)
                .from(post);

        return PageableExecutionUtils.getPage(posts, pageable, countQuery::fetchCount);*/
    }
}
