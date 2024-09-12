package sesac.server.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import sesac.server.common.dto.PageResponseDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManagerPageResponse<T> extends PageResponseDto {

    private Long campusId;

    public ManagerPageResponse(Page<T> page, Long campusId) {
        super(page);
        this.campusId = campusId;
    }
}
