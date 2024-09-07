package sesac.server.campus.repository.search;

import static sesac.server.campus.entity.QCourse.course;

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
            Pageable pageable,
            Long campusId,
            String status
    ) {
        List<Course> courses = queryFactory
                .selectFrom(course)
                .where(
                        campusIdEq(campusId),
                        statusEq(status)                // 필터링
                )
                .offset(pageable.getOffset())           // 페이징
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifiers(pageable)) // 정렬
                .fetch();

        List<ExtendedCourseResponse> content = courses.stream()
                .map(ExtendedCourseResponse::from)
                .toList();

        JPAQuery<Long> countQuery = queryFactory
                .select(course.count())
                .from(course)
                .where(
                        campusIdEq(campusId),
                        statusEq(status)
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression campusIdEq(Long campusId) {
        return campusId != null ? course.campus.id.eq(campusId) : null;
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
                String property = order.getProperty();

                switch (property) {
                    case "startDate":
                        orderSpecifiers.add(new OrderSpecifier<>(direction, course.startDate));
                        break;
                    case "name":
                        orderSpecifiers.add(new OrderSpecifier<>(direction, course.name));
                        break;
                    case "createdAt":
                        orderSpecifiers.add(new OrderSpecifier<>(direction, course.createdAt));
                        break;
                    default:
                        orderSpecifiers.add(course.startDate.desc());
                        break;
                }
            });
        }

        // 정렬 기준이 지정되지 않은 경우 기본값(개강일[최신날짜->과거]) 설정
        if (orderSpecifiers.isEmpty()) {
            orderSpecifiers.add(course.startDate.desc());
        }

        return orderSpecifiers.toArray(new OrderSpecifier[0]);
    }
}