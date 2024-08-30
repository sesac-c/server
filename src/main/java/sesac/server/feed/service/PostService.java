package sesac.server.feed.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import sesac.server.auth.exception.TokenErrorCode;
import sesac.server.auth.exception.TokenException;
import sesac.server.feed.dto.CreatePostRequest;
import sesac.server.feed.entity.Post;
import sesac.server.feed.entity.PostType;
import sesac.server.feed.repository.PostRepository;
import sesac.server.user.entity.User;
import sesac.server.user.repository.UserRepository;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public void createPost(Long userId, CreatePostRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new TokenException(TokenErrorCode.UNACCEPT));

        Post post = Post.builder()
                .type(PostType.CAMPUS)
                .title(request.title())
                .content(request.content())
                .user(user)
                .build();

        postRepository.save(post);
    }

//    public void getPost() {
//
//    }
//
//    public void getPosts() {
//
//    }
//
//    public void updatePost(Long postId) {
//    }
//
//    public void deletePost(Long postId) {
//    }


}