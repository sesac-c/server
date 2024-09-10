package sesac.server.user.dto.request;

public record AcceptStatusRequest(
        Integer statusCode,
        String rejectReason
) {

}
