package sesac.server.feed.repository.search;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sesac.server.feed.dto.request.NoticeListRequest;
import sesac.server.feed.dto.response.NoticeListResponse;
import sesac.server.feed.entity.NoticeType;

public interface NoticeSearch {

    List<NoticeListResponse> searchNotice(Pageable pageable, NoticeListRequest request,
            NoticeType type);

    Page<NoticeListResponse> searchNoticePage(Pageable pageable, NoticeListRequest request,
            NoticeType type);

}
