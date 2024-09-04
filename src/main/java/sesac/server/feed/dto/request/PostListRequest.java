package sesac.server.feed.dto.request;

import sesac.server.feed.entity.FeedType;

public record PostListRequest(
        String keyword,
        Boolean reported,
        FeedType postType
) {

}
