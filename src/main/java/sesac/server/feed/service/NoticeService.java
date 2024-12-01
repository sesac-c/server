package sesac.server.feed.service;

import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sesac.server.campus.entity.Course;
import sesac.server.campus.exception.CourseErrorCode;
import sesac.server.campus.repository.CourseRepository;
import sesac.server.common.dto.PageResponse;
import sesac.server.common.exception.BaseException;
import sesac.server.feed.dto.request.CreateNoticeRequest;
import sesac.server.feed.dto.request.NoticeListRequest;
import sesac.server.feed.dto.request.UpdateNoticeRequest;
import sesac.server.feed.dto.response.ExtendedNoticeListResponse;
import sesac.server.feed.dto.response.ImportantNoticeResponse;
import sesac.server.feed.dto.response.NoticeListResponse;
import sesac.server.feed.dto.response.NoticeResponse;
import sesac.server.feed.entity.Hashtag;
import sesac.server.feed.entity.Notice;
import sesac.server.feed.entity.NoticeType;
import sesac.server.feed.exception.PostErrorCode;
import sesac.server.feed.repository.LikesRepository;
import sesac.server.feed.repository.NoticeRepository;
import sesac.server.user.entity.User;
import sesac.server.user.service.UserService;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final LikesRepository likesRepository;

    private final CourseRepository courseRepository;
    private final UserService userService;
    private final HashtagService hashtagService;

    public Notice createNotice(Long userId, CreateNoticeRequest request,
            NoticeType noticeType) {
        Notice notice = getNotice(userId, noticeType, request);
        noticeRepository.save(notice);

        List<Hashtag> hashtags = hashtagService.saveHashTags(request.hashtags());
        hashtagService.savePostHashtags(hashtags, notice);

        return notice;
    }

    public Page<NoticeListResponse> getNoticeList(
            Pageable pageable,
            NoticeListRequest request,
            NoticeType type
    ) {

        return noticeRepository.searchNoticePage(pageable, request, type);
    }

    public NoticeResponse getNotice(Long userId, Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new BaseException(PostErrorCode.NO_POST));

        boolean likesStatus = likesRepository.existsByUserIdAndNoticeId(userId, noticeId);

        return NoticeResponse.from(userId, notice, likesStatus);
    }

    public void updateNotice(Long noticeId, UpdateNoticeRequest request) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new BaseException(PostErrorCode.NO_POST));

        notice.update(request);
        noticeRepository.save(notice);
    }

    public void deleteNotice(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new BaseException(PostErrorCode.NO_POST));

        noticeRepository.delete(notice);
    }

    public PageResponse<ExtendedNoticeListResponse> getExtendedNoticeList(
            Pageable pageable, NoticeListRequest request, NoticeType type) {

        Page<ExtendedNoticeListResponse> response = noticeRepository.searchExtendedNoticePage(
                pageable, request, type);

        return new PageResponse<>(response);
    }

    // 주요 공지
    public List<ImportantNoticeResponse> getImportantNotices() {
        return noticeRepository.findImportanceNotices();
    }

    private Notice getNotice(Long userId, NoticeType noticeType,
            CreateNoticeRequest request) {

        User user = userService.getUserOrThrowException(userId);

        return switch (noticeType) {
            case ALL -> request.toEntity(user, noticeType, null);
            case GROUP -> getGroupNotice(user, noticeType, request);
        };
    }

    private Notice getGroupNotice(User user, NoticeType noticeType,
            CreateNoticeRequest request) {
        Course course = courseRepository.findById(request.courseId()).orElseThrow(
                () -> new BaseException(CourseErrorCode.NO_COURSE)
        );
        return request.toEntity(user, noticeType, course);
    }
}


