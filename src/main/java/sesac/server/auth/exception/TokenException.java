package sesac.server.auth.exception;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import org.springframework.http.MediaType;
import sesac.server.common.exception.BaseException;
import sesac.server.common.exception.ErrorCode;

public class TokenException extends BaseException {

    public TokenException(ErrorCode errorCode) {
        super(errorCode);
    }

    public void sendResponseError(HttpServletResponse response) {
        response.setStatus(getErrorCode().getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> map = Map.of(
                "status", getErrorCode().getStatus().value(),
                "message", getErrorCode().getMessage(),
                "code", getErrorCode().getCode());

        Gson gson = new Gson();
        try {
            gson.toJson(map, response.getWriter());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
