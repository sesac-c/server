package sesac.server.feed.repository.search;

import static org.springframework.util.StringUtils.hasText;
import static sesac.server.feed.entity.QPost.post;
import static sesac.server.user.entity.QUser.user;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import sesac.server.feed.dto.request.PostListRequest;
import sesac.server.feed.dto.response.PostListResponse;
import sesac.server.feed.entity.Post;
import sesac.server.feed.entity.FeedType;

@RequiredArgsConstructor
public class PostSearchImpl implements PostSearch {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<PostListResponse> searchPost(
            Pageable pageable,
            PostListRequest request,
            FeedType type
    ) {

        List<Post> postList = queryFactory
                .selectFrom(post)
                .join(post.user, user)
                .where(
                        typeEq(type),
                        titleLike(request.keyword())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(post.id.desc())
                .fetch();

        List<PostListResponse> posts = postList.stream().map(PostListResponse::new).toList();

        return posts;
    }

    @Override
    public Page<PostListResponse> searchPostPage(
            Pageable pageable,
            PostListRequest request,
            FeedType type
    ) {

        List<PostListResponse> posts = this.searchPost(pageable, request, type);

        JPAQuery<Post> countQuery = queryFactory
                .select(post)
                .where(
                        typeEq(request.postType()),
                        titleLike(request.keyword())
                )
                .from(post);

        return PageableExecutionUtils.getPage(posts, pageable, countQuery::fetchCount);
    }

    private BooleanExpression typeEq(FeedType type) {
        return type != null ? post.type.eq(type) : null;
    }

    private BooleanExpression titleLike(String keyword) {
        return hasText(keyword) ? post.title.contains(keyword) : null;
    }
}
