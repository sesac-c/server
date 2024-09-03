package sesac.server.account.dto.request;

public record LogoutRequest(String accessToken, String refreshToken) {

}
