package sesac.server.manager.service;

import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sesac.server.auth.exception.TokenErrorCode;
import sesac.server.auth.exception.TokenException;
import sesac.server.common.exception.BaseException;
import sesac.server.feed.dto.request.CreateNoticeRequest;
import sesac.server.feed.dto.request.NoticeListRequest;
import sesac.server.feed.dto.request.UpdateNoticeRequest;
import sesac.server.feed.dto.response.NoticeListResponse;
import sesac.server.feed.dto.response.NoticeResponse;
import sesac.server.feed.entity.FeedType;
import sesac.server.feed.entity.Hashtag;
import sesac.server.feed.entity.Notice;
import sesac.server.feed.entity.NoticeType;
import sesac.server.feed.entity.PostHashtag;
import sesac.server.feed.exception.PostErrorCode;
import sesac.server.feed.repository.HashtagRepository;
import sesac.server.feed.repository.NoticeRepository;
import sesac.server.feed.repository.PostHashtagRepository;
import sesac.server.user.entity.User;
import sesac.server.user.repository.UserRepository;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class ManagerNoticeService {

    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;
    private final HashtagRepository hashtagRepository;
    private final PostHashtagRepository postHashtagRepository;

    public Notice createNotice(Long userId, CreateNoticeRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new TokenException(TokenErrorCode.UNACCEPT));

        Notice notice = Notice.builder()
                .user(user)
                .title(request.title())
                .content(request.content())
                .type(request.type())
                .importance(request.importance())
                .image(request.image())
                .status(true)
                .build();

        noticeRepository.save(notice);

        List<Hashtag> hashtags = hashtagRepository.findByNameIn(request.hashtags());
        List<Hashtag> newHashtags = request.hashtags()
                .stream()
                .filter(hashtag -> !hashtags.stream()
                        .map(r -> r.getName())
                        .toList()
                        .contains(hashtag))
                .map(hashtag -> Hashtag.builder()
                        .name(hashtag)
                        .build())
                .toList();

        hashtags.addAll(newHashtags);

        hashtagRepository.saveAll(hashtags);

        List<PostHashtag> postHashtags = hashtags.stream()
                .map(hashtag -> PostHashtag.builder()
                        .notice(notice)
                        .hashtag(hashtag)
                        .type(FeedType.NOTICE)
                        .build())
                .toList();

        postHashtagRepository.saveAll(postHashtags);

        return notice;
    }

    public Page<NoticeListResponse> getNoticeList(
            Pageable pageable,
            NoticeListRequest request,
            NoticeType type
    ) {

        return noticeRepository.searchNoticePage(pageable, request, type);
    }

    public NoticeResponse getNotice(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new BaseException(PostErrorCode.NO_POST));

//        List<ReplyResponse> replies = notice.getReplies().stream()
//                .map(ReplyResponse::new)
//                .toList();
//
//        return new PostResponse(post, replies);
        return new NoticeResponse(notice);
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
}
