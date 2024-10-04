package sesac.server.user.repository.search;

import static sesac.server.campus.entity.QCampus.campus;
import static sesac.server.campus.entity.QCourse.course;
import static sesac.server.user.entity.QCourseChangeRequest.courseChangeRequest;
import static sesac.server.user.entity.QStudent.student;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import sesac.server.common.util.JPAQueryUtil;
import sesac.server.user.dto.response.CourseChangeRequestResponse;
import sesac.server.user.dto.response.QCourseChangeRequestResponse;
import sesac.server.user.entity.QCourseChangeRequest;

@Log4j2
@RequiredArgsConstructor
public class CourseChangeRequestSearchImpl implements CourseChangeRequestSearch {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<CourseChangeRequestResponse> searchCourseChangeRequests(
            Pageable pageable, String status
    ) {
        List<CourseChangeRequestResponse> courseChangeRequestResponses = applyCommonConditions(
                queryFactory
                        .select(new QCourseChangeRequestResponse(
                                courseChangeRequest.id,
                                student.id,
                                student.name,
                                course.id,
                                course.name,
                                campus.name,
                                courseChangeRequest.statusCode,
                                courseChangeRequest.createdAt
                        ))
                        .from(courseChangeRequest),
                status
        )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(courseChangeRequestOrderBy(courseChangeRequest, pageable.getSort()))
                .fetch();

        JPAQuery<Long> countQuery = applyCommonConditions(
                queryFactory.select(courseChangeRequest.count())
                        .from(courseChangeRequest),
                status
        );

        return PageableExecutionUtils.getPage(courseChangeRequestResponses, pageable,
                countQuery::fetchOne);
    }

    private <T> JPAQuery<T> applyCommonConditions(JPAQuery<T> query, String status) {
        return query
                .leftJoin(student).on(student.id.eq(courseChangeRequest.student.id))
                .leftJoin(course).on(course.eq(courseChangeRequest.newCourse))
                .leftJoin(campus).on(campus.id.eq(course.campus.id))
                .where(statusEq(status));
    }

    private BooleanExpression statusEq(String statusCode) {
        return statusCode != null ? courseChangeRequest.statusCode.eq(Integer.parseInt(statusCode))
                : null;
    }

    private OrderSpecifier<?>[] courseChangeRequestOrderBy(QCourseChangeRequest courseChangeRequest,
            Sort sort) {
        List<OrderSpecifier<?>> orderSpecifiers = JPAQueryUtil.getOrderSpecifiers(
                courseChangeRequest, sort);

        orderSpecifiers.add(courseChangeRequest.id.desc());

        return orderSpecifiers.toArray(OrderSpecifier[]::new);
    }
}
