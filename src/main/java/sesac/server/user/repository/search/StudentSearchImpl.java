package sesac.server.user.repository.search;

import static sesac.server.campus.entity.QCampus.campus;
import static sesac.server.campus.entity.QCourse.course;
import static sesac.server.user.entity.QStudent.student;
import static sesac.server.user.entity.QUser.user;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import sesac.server.auth.dto.CustomPrincipal;
import sesac.server.common.exception.BaseException;
import sesac.server.common.util.JPAQueryUtil;
import sesac.server.user.dto.request.SearchStudentRequest;
import sesac.server.user.dto.response.QSearchStudentResponse;
import sesac.server.user.dto.response.QStudentProfileFormResponse;
import sesac.server.user.dto.response.SearchStudentResponse;
import sesac.server.user.dto.response.StudentProfileFormResponse;
import sesac.server.user.entity.QStudent;
import sesac.server.user.entity.Student;
import sesac.server.user.exception.UserErrorCode;

@Log4j2
@RequiredArgsConstructor
public class StudentSearchImpl implements StudentSearch {

    private final JPAQueryFactory queryFactory;

    private static final String DEFAULT_PROFILE_IMAGE = "default-profile.png";

    @Override
    public Page<SearchStudentResponse> searchStudent(
            Long campusId,
            Pageable pageable,
            SearchStudentRequest request
    ) {

        List<SearchStudentResponse> students = queryFactory
                .select(new QSearchStudentResponse(
                        student.id,
                        student.name,
                        user.email,
                        course.id,
                        course.name,
                        student.statusCode,
                        user.createdAt
                ))
                .from(student)
                .join(student.user, user)
                .join(student.firstCourse, course)
                .join(course.campus, campus)
                .where(
                        campus.id.eq(campusId),
                        nameContains(request.name()),
                        courseEq(request.courseId()),
                        statusEq(request.status())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(studentOrderBy(student, pageable.getSort()))
                .fetch();

        JPAQuery<Student> countQuery = queryFactory
                .select(student)
                .where(
                        student.firstCourse.campus.id.eq(campusId),
                        nameContains(request.name()),
                        courseEq(request.courseId()),
                        statusEq(request.status())
                )
                .from(student);

        return PageableExecutionUtils.getPage(students, pageable, countQuery::fetchCount);

    }

    @Override
    public StudentProfileFormResponse getStudentProfileFormResponse(CustomPrincipal principal) {
        QStudent student = QStudent.student;

        StudentProfileFormResponse result = queryFactory
                .select(new QStudentProfileFormResponse(
                        getStudentProfileImageExpression(student),
                        student.nickname,
                        student.firstCourse.campus.id,
                        getFormattedCampus(),
                        student.firstCourse.id,
                        getFormattedCourse()
                ))
                .from(student)
                .where(student.id.eq(principal.id()))
                .fetchOne();

        if (result == null) {
            throw new BaseException(UserErrorCode.NO_USER);
        }
        return result;
    }

    private BooleanExpression statusEq(Integer statusCode) {
        return statusCode != null ? student.statusCode.eq(statusCode) : null;
    }

    private BooleanExpression nameContains(String name) {
        return name != null ? student.name.contains(name) : null;
    }

    private BooleanExpression courseEq(Long courseId) {
        return courseId != null ? student.firstCourse.id.eq(courseId) : null;
    }

    private OrderSpecifier<?>[] studentOrderBy(QStudent student, Sort sort) {
        List<OrderSpecifier<?>> orderSpecifiers = JPAQueryUtil.getOrderSpecifiers(student, sort);

        orderSpecifiers.add(student.id.desc());

        return orderSpecifiers.toArray(OrderSpecifier[]::new);
    }


    private Expression<String> getStudentProfileImageExpression(QStudent student) {
        return new CaseBuilder()
                .when(student.profileImage.isNotNull())
                .then(student.profileImage)
                .otherwise(DEFAULT_PROFILE_IMAGE);
    }

    private Expression<String> getFormattedCampus() {
        return Expressions.stringTemplate(
                "CONCAT({0}, ' 캠퍼스')",
                Expressions.asString(student.firstCourse.campus.name)
        );
    }

    private Expression<String> getFormattedCourse() {
        return Expressions.stringTemplate(
                "CONCAT('(', {0}, '기)', {1})",
                Expressions.asString(student.firstCourse.classNumber),
                Expressions.asString(student.firstCourse.name)
        );
    }
}
