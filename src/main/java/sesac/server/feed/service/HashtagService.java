package sesac.server.feed.service;

import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sesac.server.feed.entity.Hashtag;
import sesac.server.feed.entity.Notice;
import sesac.server.feed.entity.Post;
import sesac.server.feed.entity.PostHashtag;
import sesac.server.feed.repository.HashtagRepository;
import sesac.server.feed.repository.PostHashtagRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class HashtagService {

    private final HashtagRepository hashtagRepository;
    private final PostHashtagRepository postHashtagRepository;

    public List<Hashtag> saveHashTags(List<String> tags) {
        List<Hashtag> hashtags = hashtagRepository.findByNameIn(tags);
        List<Hashtag> newHashtags = tags
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

        return hashtags;
    }

    public void savePostHashtags(List<Hashtag> hashtags, Post post, Notice notice) {
        List<PostHashtag> postHashtags = hashtags.stream()
                .map(hashtag -> PostHashtag.builder()
                        .post(post)
                        .notice(notice)
                        .hashtag(hashtag)
                        .build())
                .toList();

        postHashtagRepository.saveAll(postHashtags);
    }
}
