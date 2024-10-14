package sesac.server.feed.repository.search;

import static org.springframework.util.StringUtils.hasText;
import static sesac.server.feed.entity.QPost.post;
import static sesac.server.user.entity.QStudent.student;
import static sesac.server.user.entity.QUser.user;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import sesac.server.feed.dto.request.PostListRequest;
import sesac.server.feed.dto.response.ExtendedPostListResponse;
import sesac.server.feed.dto.response.PopularPostResponse;
import sesac.server.feed.dto.response.PostListResponse;
import sesac.server.feed.dto.response.QExtendedPostListResponse;
import sesac.server.feed.dto.response.QPopularPostResponse;
import sesac.server.feed.entity.Post;
import sesac.server.feed.entity.PostType;

@RequiredArgsConstructor
public class PostSearchImpl implements PostSearch {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<PostListResponse> searchPost(
            Pageable pageable,
            PostListRequest request,
            PostType type
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
            PostType type
    ) {

        List<PostListResponse> posts = this.searchPost(pageable, request, type);

        JPAQuery<Post> countQuery = queryFactory
                .select(post)
                .where(
                        typeEq(type),
                        titleLike(request.keyword())
                )
                .from(post);

        return PageableExecutionUtils.getPage(posts, pageable, countQuery::fetchCount);
    }

    @Override
    public Page<ExtendedPostListResponse> searchExtendedPostPage(Pageable pageable,
            PostListRequest request, PostType type) {
        List<ExtendedPostListResponse> posts = queryFactory
                .select(new QExtendedPostListResponse(
                        post.id,
                        student.nickname,
                        post.title,
                        post.content,
                        post.type,
                        post.createdAt
                ))
                .from(post)
                .join(post.user, user)
                .join(user.student, student)
                .where(
                        typeEq(type),
                        titleLike(request.keyword())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(post.id.desc())
                .fetch();

        JPAQuery<Post> countQuery = queryFactory
                .select(post)
                .where(
                        typeEq(type),
                        titleLike(request.keyword())
                )
                .from(post);

        return PageableExecutionUtils.getPage(posts, pageable, countQuery::fetchCount);
    }

    @Override
    public List<PopularPostResponse> popularPosts() {
        LocalDateTime now = LocalDateTime.now();
        List<PopularPostResponse> posts = queryFactory
                .select(new QPopularPostResponse(
                        post.id,
                        post.title,
                        post.type
                ))
                .from(post)
                .where(
                        post.createdAt.between(now.minusHours(24), now),
                        post.likesCount.gt(0)
                )
                .limit(10)
                .orderBy(post.likesCount.desc(), post.id.desc())
                .fetch();

        return posts;
    }

    private BooleanExpression typeEq(PostType type) {
        return type != null ? post.type.eq(type) : null;
    }

    private BooleanExpression titleLike(String keyword) {
        return hasText(keyword) ? post.title.contains(keyword) : null;
    }
}
