package sesac.server.user.repository.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sesac.server.user.dto.request.SearchStudentRequest;
import sesac.server.user.dto.response.SearchStudentResponse;

public interface StudentSearch {

    Page<SearchStudentResponse> searchStudent(
            Long campusId, Pageable pageable, SearchStudentRequest request);
}
