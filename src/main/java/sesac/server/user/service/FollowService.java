package sesac.server.user.service;

import org.springframework.stereotype.Service;
import sesac.server.auth.dto.CustomPrincipal;
import sesac.server.common.exception.BaseException;
import sesac.server.user.entity.Follow;
import sesac.server.user.entity.User;
import sesac.server.user.exception.FollowErrorCode;
import sesac.server.user.repository.FollowRepository;
import sesac.server.user.repository.ManagerRepository;
import sesac.server.user.repository.StudentRepository;
import sesac.server.user.repository.UserRepository;

@Service
public class FollowService extends CommonUserService {

    private final FollowRepository followRepository;

    public FollowService(UserRepository userRepository,
            StudentRepository studentRepository,
            ManagerRepository managerRepository,
            FollowRepository followRepository
    ) {
        super(userRepository, studentRepository, managerRepository);
        this.followRepository = followRepository;
    }

    public void followUser(CustomPrincipal principal, Long userId) {
        Long followerId = principal.id();

        if (followerId.equals(userId)) {
            throw new BaseException(FollowErrorCode.SELF_FOLLOW_NOT_ALLOWED);
        }

        User follower = getUserOrThrowException(followerId);
        User following = getUserOrThrowException(userId);

        // 이미 팔로우한 경우
        if (followRepository.existsByFollowerAndFollowing(follower, following)) {
            throw new BaseException(FollowErrorCode.ALREADY_FOLLOWING);
        }

        Follow follow = Follow.builder()
                .follower(follower)
                .following(following)
                .build();

        followRepository.save(follow);
    }

    public void unfollowUser(CustomPrincipal principal, Long userId) {
        removeFollowRelation(principal.id(), userId, FollowErrorCode.SELF_UNFOLLOW_NOT_ALLOWED);
    }

    public void removeFollowing(CustomPrincipal principal, Long userId) {
        removeFollowRelation(userId, principal.id(),
                FollowErrorCode.SELF_REMOVE_FOLLOWING_NOT_ALLOWED);
    }

    private void removeFollowRelation(Long followerId, Long followingId,
            FollowErrorCode selfActionError) {
        if (followerId.equals(followingId)) {
            throw new BaseException(selfActionError);
        }

        User follower = getUserOrThrowException(followerId);
        User following = getUserOrThrowException(followingId);

        Follow follow = followRepository.findByFollowerAndFollowing(follower, following)
                .orElseThrow(() -> new BaseException(FollowErrorCode.NOT_FOLLOWING));

        followRepository.delete(follow);
    }
}
