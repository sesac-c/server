package sesac.server.feed.repository.search;

import static org.springframework.util.StringUtils.hasText;
import static sesac.server.feed.entity.QNotice.notice;
import static sesac.server.user.entity.QUser.user;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import sesac.server.feed.dto.request.NoticeListRequest;
import sesac.server.feed.dto.response.ExtendedNoticeListResponse;
import sesac.server.feed.dto.response.NoticeListResponse;
import sesac.server.feed.dto.response.QExtendedNoticeListResponse;
import sesac.server.feed.entity.Notice;
import sesac.server.feed.entity.NoticeType;

@RequiredArgsConstructor
public class NoticeSearchImpl implements NoticeSearch {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<NoticeListResponse> searchNotice(
            Pageable pageable,
            NoticeListRequest request,
            NoticeType type
    ) {

        List<Notice> noticeList = queryFactory
                .selectFrom(notice)
                .join(notice.user, user)
                .where(
                        typeEq(type),
                        titleLike(request.keyword())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(notice.id.desc())
                .fetch();

        List<NoticeListResponse> notices = noticeList.stream().map(NoticeListResponse::new)
                .toList();

        return notices;
    }

    @Override
    public Page<NoticeListResponse> searchNoticePage(
            Pageable pageable,
            NoticeListRequest request,
            NoticeType type
    ) {

        List<NoticeListResponse> notices = this.searchNotice(pageable, request, type);

        JPAQuery<Notice> countQuery = queryFactory
                .select(notice)
                .where(
                        typeEq(type),
                        titleLike(request.keyword())
                )
                .from(notice);

        return PageableExecutionUtils.getPage(notices, pageable, countQuery::fetchCount);
    }

    @Override
    public Page<ExtendedNoticeListResponse> searchExtendedNoticePage(Pageable pageable,
            NoticeListRequest request, NoticeType type) {
        List<ExtendedNoticeListResponse> notices = queryFactory
                .select(new QExtendedNoticeListResponse(
                        notice.id,
                        user.manager.campus.name,
                        notice.title,
                        notice.content,
                        notice.type,
                        notice.createdAt
                ))
                .from(notice)
                .join(notice.user, user)
                .where(
                        typeEq(type),
                        titleLike(request.keyword())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(notice.id.desc())
                .fetch();

        JPAQuery<Notice> countQuery = queryFactory
                .select(notice)
                .where(
                        typeEq(type),
                        titleLike(request.keyword())
                )
                .from(notice);

        return PageableExecutionUtils.getPage(notices, pageable, countQuery::fetchCount);

    }

    private BooleanExpression typeEq(NoticeType type) {
        return type != null ? notice.type.eq(type) : null;
    }

    private BooleanExpression titleLike(String keyword) {
        return hasText(keyword) ? notice.title.contains(keyword) : null;
    }

}