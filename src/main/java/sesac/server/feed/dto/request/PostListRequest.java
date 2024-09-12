package sesac.server.feed.dto.request;

public record PostListRequest(
        String keyword,
        Boolean reported
) {

}
