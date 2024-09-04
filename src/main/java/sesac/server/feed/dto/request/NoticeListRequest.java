package sesac.server.feed.dto.request;

import sesac.server.feed.entity.NoticeType;

public record NoticeListRequest(
        String keyword,
        Boolean reported,
        NoticeType type
) {

}
