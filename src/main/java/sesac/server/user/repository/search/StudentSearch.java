package sesac.server.user.repository.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sesac.server.auth.dto.CustomPrincipal;
import sesac.server.user.dto.request.SearchStudentRequest;
import sesac.server.user.dto.response.SearchStudentResponse;
import sesac.server.user.dto.response.StudentProfileFormResponse;

public interface StudentSearch {

    Page<SearchStudentResponse> searchStudent(
            Long campusId, Pageable pageable, SearchStudentRequest request);

    StudentProfileFormResponse getStudentProfileFormResponse(CustomPrincipal principal);
}
