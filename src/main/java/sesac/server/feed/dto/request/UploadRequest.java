package sesac.server.feed.dto.request;

import java.util.List;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UploadRequest {

    private List<MultipartFile> files;
}
