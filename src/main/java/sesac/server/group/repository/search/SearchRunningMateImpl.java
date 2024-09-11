package sesac.server.group.repository.search;

import static sesac.server.campus.entity.QCampus.campus;
import static sesac.server.campus.entity.QCourse.course;
import static sesac.server.group.entity.QRunningMate.runningMate;
import static sesac.server.group.entity.QRunningMateMember.runningMateMember;
import static sesac.server.user.entity.QStudent.student;
import static sesac.server.user.entity.QUser.user;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import sesac.server.group.dto.request.SearchRunningMateRequest;
import sesac.server.group.dto.response.QRunningMateMemberListResponse;
import sesac.server.group.dto.response.QSearchRunningMateResponse;
import sesac.server.group.dto.response.RunningMateMemberListResponse;
import sesac.server.group.dto.response.SearchRunningMateResponse;
import sesac.server.group.entity.RunningMate;

@Log4j2
@RequiredArgsConstructor
public class SearchRunningMateImpl implements SearchRunningMate {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<SearchRunningMateResponse> runningMateSearch(Pageable pageable,
            SearchRunningMateRequest request) {
        List<SearchRunningMateResponse> content = queryFactory
                .select(new QSearchRunningMateResponse(
                        runningMate.id,
                        runningMate.name,
                        runningMate.subject,
                        runningMate.goal,
                        runningMate.course.name
                ))
                .from(runningMate)
                .where(
                        courseEq(request.course()),
                        nameContains(request.name())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrders(pageable.getSort()))
                .fetch();

        JPAQuery<RunningMate> countQuery = queryFactory
                .select(runningMate)
                .where(
                        courseEq(request.course()),
                        nameContains(request.name())
                )
                .from(runningMate);
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchCount);
    }

    private BooleanExpression courseEq(Long courseId) {
        return courseId != null ? runningMate.course.id.eq(courseId) : null;
    }


    private BooleanExpression nameContains(String name) {
        return name != null ? runningMate.name.contains(name) : null;
    }

    private OrderSpecifier<?>[] getOrders(Sort sort) {
        List<OrderSpecifier<?>> orderSpecifiers = getOrderSpecifiers(runningMate, sort);

        if (orderSpecifiers.isEmpty()) {
            orderSpecifiers.add(runningMate.id.desc());
        }

        return orderSpecifiers.toArray(OrderSpecifier[]::new);
    }

    private List<OrderSpecifier<?>> getOrderSpecifiers(EntityPathBase<?> qType, Sort sort) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

        for (Sort.Order order : sort) {
            PathBuilder entityPath = new PathBuilder<>(qType.getType(), qType.getMetadata());
            OrderSpecifier orderSpecifier = new OrderSpecifier(
                    order.isAscending() ? Order.ASC : Order.DESC,
                    entityPath.get(order.getProperty())
            );
            orderSpecifiers.add(orderSpecifier);
        }

        return orderSpecifiers;
    }

    @Override
    public boolean existsMember(Long runningMateId, Long userId, String phoneNumber) {
        Integer result = queryFactory
                .selectOne()
                .from(runningMateMember)
                .where(

                        runningMateMember.runningMate.id.eq(runningMateId)
                                .and(runningMateMember.user.id.eq(userId))
                                .or(runningMateMember.phoneNumber.eq(phoneNumber))
                )
                .fetchFirst();
        return result != null;
    }

    @Override
    public List<RunningMateMemberListResponse> runningMateMembers(Long runningMateId) {
        List<RunningMateMemberListResponse> result = queryFactory
                .select(new QRunningMateMemberListResponse(
                        user.id,
                        student.name,
                        course.name,
                        campus.name,
                        runningMateMember.role,
                        runningMateMember.phoneNumber
                ))
                .from(runningMateMember)
                .join(runningMateMember.user, user)
                .join(user.student, student)
                .join(runningMateMember.runningMate, runningMate)
                .join(runningMate.course, course)
                .join(course.campus, campus)
                .where(
                        runningMateMember.runningMate.id.eq(runningMateId)
                )
                .fetch();

        return result;
    }
}
