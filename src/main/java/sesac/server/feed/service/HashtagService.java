package sesac.server.feed.service;

import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
        Set<String> tagsSet = new HashSet<>(hashtags.stream()
                .map(Hashtag::getName).toList());

        List<Hashtag> newTags = tags
                .stream()
                .filter(tag -> !tagsSet.contains(tag))
                .map(tag -> Hashtag.builder().name(tag).build())
                .toList();

        hashtags.addAll(newTags);
        hashtagRepository.saveAll(newTags);

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
