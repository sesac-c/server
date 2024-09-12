package sesac.server.feed.dto.request;

public record NoticeListRequest(
        String keyword,
        Boolean reported
) {

}
