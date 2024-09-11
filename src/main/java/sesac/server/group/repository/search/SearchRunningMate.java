package sesac.server.group.repository.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sesac.server.group.dto.request.SearchRunningMateRequest;
import sesac.server.group.dto.response.SearchRunningMateResponse;

public interface SearchRunningMate {

    Page<SearchRunningMateResponse> runningMateSearch(Pageable pageable,
            SearchRunningMateRequest request);
}
