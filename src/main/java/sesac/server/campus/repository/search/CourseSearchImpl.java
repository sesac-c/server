package sesac.server.campus.repository.search;

import static sesac.server.campus.entity.QCourse.course;
import static sesac.server.user.entity.QManager.manager;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import sesac.server.campus.dto.response.ExtendedCourseResponse;
import sesac.server.campus.entity.Course;

@RequiredArgsConstructor
public class CourseSearchImpl implements CourseSearch {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ExtendedCourseResponse> searchCourse(
            Long managerId,
            Pageable pageable,
            String status
    ) {
        List<Course> courses = getCourseQuery(managerId, status)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifiers(pageable))
                .fetch();

        List<ExtendedCourseResponse> content = courses.stream()
                .map(ExtendedCourseResponse::from)
                .toList();

        JPAQuery<Long> countQuery = getCountQuery(managerId, status);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private JPAQuery<Course> getCourseQuery(Long managerId, String status) {
        return queryFactory
                .selectFrom(course)
                .join(manager).on(course.campus.id.eq(manager.campus.id))
                .where(
                        managerIdEq(managerId),
                        statusEq(status)
                );
    }

    private JPAQuery<Long> getCountQuery(Long managerId, String status) {
        return queryFactory
                .select(course.count())
                .from(course)
                .join(manager).on(course.campus.id.eq(manager.campus.id))
                .where(
                        managerIdEq(managerId),
                        statusEq(status)
                );
    }

    private BooleanExpression managerIdEq(Long managerId) {
        return managerId != null ? manager.id.eq(managerId) : null;
    }

    private BooleanExpression statusEq(String status) {
        if (status == null) {
            return null;
        }
        LocalDate now = LocalDate.now();
        return switch (status) {
            case "upcoming" -> course.startDate.after(now);
            case "ongoing" -> course.startDate.before(now).and(course.endDate.after(now));
            case "finished" -> course.endDate.before(now);
            default -> null;
        };
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(Pageable pageable) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

        if (pageable.getSort().isSorted()) {
            pageable.getSort().forEach(order -> {
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
                switch (order.getProperty()) {
                    case "startDate" ->
                            orderSpecifiers.add(new OrderSpecifier<>(direction, course.startDate));
                    case "name" ->
                            orderSpecifiers.add(new OrderSpecifier<>(direction, course.name));
                    case "createdAt" ->
                            orderSpecifiers.add(new OrderSpecifier<>(direction, course.createdAt));
                    default ->
                            orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, course.startDate));
                }
            });
        }

        if (orderSpecifiers.isEmpty()) {
            orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, course.startDate));
        }

        return orderSpecifiers.toArray(new OrderSpecifier[0]);
    }
}