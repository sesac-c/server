package sesac.server.feed.dto.request;

import sesac.server.feed.entity.PostType;

public record PostListRequest(
        String keyword,
        Boolean reported,
        PostType postType
) {

}
