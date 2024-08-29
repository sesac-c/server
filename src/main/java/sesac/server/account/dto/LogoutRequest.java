package sesac.server.account.dto;

public record LogoutRequest(String accessToken, String refreshToken) {

}
