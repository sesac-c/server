package sesac.server.feed.dto;

import sesac.server.feed.entity.PostType;

public record PostListRequest(
        String keyword,
        Boolean reported,
        PostType postType
) {

}
