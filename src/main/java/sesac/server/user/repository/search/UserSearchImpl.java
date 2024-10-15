package sesac.server.user.repository.search;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import sesac.server.auth.dto.CustomPrincipal;
import sesac.server.campus.entity.QCampus;
import sesac.server.campus.entity.QCourse;
import sesac.server.common.constants.AppConstants;
import sesac.server.common.exception.BaseException;
import sesac.server.user.dto.response.ProfileResponse;
import sesac.server.user.dto.response.QProfileResponse;
import sesac.server.user.entity.QFollow;
import sesac.server.user.entity.QManager;
import sesac.server.user.entity.QStudent;
import sesac.server.user.entity.QUser;
import sesac.server.user.exception.UserErrorCode;

@Log4j2
@RequiredArgsConstructor
public class UserSearchImpl implements UserSearch {

    private static final String UNKNOWN = "Unknown";

    private final JPAQueryFactory queryFactory;

    @Override
    public ProfileResponse getProfileResponse(Long profileUserId, CustomPrincipal principal) {
        QUser user = QUser.user;
        QStudent student = QStudent.student;
        QManager manager = QManager.manager;
        QFollow follow = QFollow.follow;
        QCourse course = QCourse.course;
        QCampus campus = QCampus.campus;

        ProfileResponse result = queryFactory
                .select(new QProfileResponse(
                        getNickname(student, manager),
                        getAffiliationExpression(student, manager),
                        getProfileImageExpression(student, manager),
                        user.id.eq(principal.id()),
                        getFollowingCountSubquery(follow, profileUserId),
                        getFollowerCountSubquery(follow, profileUserId),
                        getIsFollowingExpression(follow, profileUserId, principal.id())
                ))
                .from(user)
                .leftJoin(student).on(user.id.eq(student.id))
                .leftJoin(manager).on(user.id.eq(manager.id))
                .leftJoin(student.firstCourse, course)
                .leftJoin(manager.campus, campus)
                .where(user.id.eq(profileUserId))
                .fetchOne();

        validateProfileResponse(result);

        return result;
    }

    private Expression<String> getNickname(QStudent student, QManager manager) {
        Expression<String> formattedCampusInfo = Expressions.stringTemplate(
                "CONCAT({0}, ' 캠퍼스')",
                Expressions.asString(manager.campus.name)
        );

        return new CaseBuilder()
                .when(manager.isNotNull())
                .then(formattedCampusInfo)
                .when(student.isNotNull())
                .then(student.nickname)
                .otherwise(UNKNOWN);
    }

    private Expression<String> getAffiliationExpression(QStudent student, QManager manager) {
        Expression<String> formattedCourseInfo = Expressions.stringTemplate(
                "CONCAT('(', {0}, '기) ', {1})",
                Expressions.asString(student.firstCourse.classNumber),
                Expressions.asString(student.firstCourse.name)
        );

        Expression<String> formattedCampusInfo = Expressions.stringTemplate(
                "CONCAT({0}, ' 캠퍼스')",
                Expressions.asString(manager.campus.name)
        );

        return new CaseBuilder()
                .when(manager.isNotNull())
                .then(formattedCampusInfo)
                .when(student.isNotNull())
                .then(formattedCourseInfo)
                .otherwise(UNKNOWN);
    }

    private Expression<String> getProfileImageExpression(QStudent student, QManager manager) {
        return new CaseBuilder()
                .when(manager.isNotNull().and(manager.profileImage.isNotNull()))
                .then(manager.profileImage)
                .when(student.isNotNull().and(student.profileImage.isNotNull()))
                .then(student.profileImage)
                .otherwise(AppConstants.DEFAULT_PROFILE_IMAGE);
    }

    private Expression<Long> getFollowerCountSubquery(QFollow follow, Long profileUserId) {
        return JPAExpressions.select(follow.count())
                .from(follow)
                .where(follow.following.id.eq(profileUserId));
    }

    private Expression<Long> getFollowingCountSubquery(QFollow follow, Long profileUserId) {
        return JPAExpressions.select(follow.count())
                .from(follow)
                .where(follow.follower.id.eq(profileUserId));
    }

    private void validateProfileResponse(ProfileResponse result) {
        if (result == null) {
            throw new BaseException(UserErrorCode.NO_USER);
        }
        if (UNKNOWN.equals(result.nickname()) || UNKNOWN.equals(result.affiliation())) {
            throw new BaseException(UserErrorCode.INVALID_USER_ROLE);
        }
    }

    private Expression<Boolean> getIsFollowingExpression(QFollow follow, Long profileUserId,
            Long principalId) {
        return JPAExpressions
                .selectOne()
                .from(follow)
                .where(follow.follower.id.eq(principalId)
                        .and(follow.following.id.eq(profileUserId)))
                .exists();
    }
}