package sesac.server.sample;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import sesac.server.auth.dto.AuthPrincipal;
import sesac.server.auth.dto.PrincipalRecord;
import sesac.server.common.exception.BaseException;
import sesac.server.common.exception.GlobalErrorCode;

@RestController
@Log4j2
public class SampleController {

    @GetMapping
    public String hello(@AuthPrincipal PrincipalRecord principal) {
        log.info("hello, {}", GlobalErrorCode.INTERNAL_SERVER_ERROR.getStatus());
        log.info("hello,   {}", GlobalErrorCode.INTERNAL_SERVER_ERROR.getMessage());
        log.info("hello, {}", GlobalErrorCode.INVALID.getMessage());
        throw new BaseException(GlobalErrorCode.INTERNAL_SERVER_ERROR);

    }
}
