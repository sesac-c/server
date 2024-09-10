package sesac.server.user.repository.search;

import static sesac.server.campus.entity.QCampus.campus;
import static sesac.server.campus.entity.QCourse.course;
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
import sesac.server.user.dto.request.SearchStudentRequest;
import sesac.server.user.dto.response.QSearchStudentResponse;
import sesac.server.user.dto.response.SearchStudentResponse;
import sesac.server.user.entity.QStudent;
import sesac.server.user.entity.Student;

@Log4j2
@RequiredArgsConstructor
public class StudentSearchImpl implements StudentSearch {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<SearchStudentResponse> searchStudent(
            Pageable pageable,
            SearchStudentRequest request
    ) {

        List<SearchStudentResponse> students = queryFactory
                .select(new QSearchStudentResponse(
                        student.id,
                        student.name,
                        user.email,
                        campus.name,
                        course.name,
                        student.statusCode,
                        user.createdAt
                ))
                .from(student)
                .join(student.user, user)
                .join(student.firstCourse, course)
                .join(course.campus, campus)
                .where(
                        nameContains(request.name()),
                        courseContains(request.course()),
                        statusEq(request.status())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(studentOrderBy(student, pageable.getSort()))
                .fetch();

        JPAQuery<Student> countQuery = queryFactory
                .select(student)
                .where(
                        nameContains(request.name()),
                        courseContains(request.course()),
                        statusEq(request.status())
                )
                .from(student);

        return PageableExecutionUtils.getPage(students, pageable, countQuery::fetchCount);

    }

    private BooleanExpression statusEq(Integer statusCode) {
        return statusCode != null ? student.statusCode.eq(statusCode) : null;
    }

    private BooleanExpression nameContains(String name) {
        return name != null ? student.name.contains(name) : null;
    }

    private BooleanExpression courseContains(String course) {
        return course != null ? student.firstCourse.name.contains(course) : null;
    }

    private OrderSpecifier<?>[] studentOrderBy(QStudent student, Sort sort) {
        List<OrderSpecifier<?>> orderSpecifiers = getOrderSpecifiers(student, sort);

        if (orderSpecifiers.isEmpty()) {
            orderSpecifiers.add(student.id.desc());
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

}
