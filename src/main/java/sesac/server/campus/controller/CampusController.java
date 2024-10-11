package sesac.server.campus.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sesac.server.auth.dto.AuthPrincipal;
import sesac.server.auth.dto.CustomPrincipal;
import sesac.server.campus.dto.request.CreateCampusRequest;
import sesac.server.campus.dto.request.CreateCourseRequest;
import sesac.server.campus.dto.request.UpdateCampusRequest;
import sesac.server.campus.dto.request.UpdateCourseRequest;
import sesac.server.campus.dto.response.CampusDetailResponse;
import sesac.server.campus.dto.response.CampusResponse;
import sesac.server.campus.dto.response.CourseDetailResponse;
import sesac.server.campus.dto.response.CourseResponse;
import sesac.server.campus.dto.response.ExtendedCourseResponse;
import sesac.server.campus.exception.CampusErrorCode;
import sesac.server.campus.exception.CourseErrorCode;
import sesac.server.campus.service.CampusService;
import sesac.server.campus.service.CourseService;
import sesac.server.common.dto.PageResponse;
import sesac.server.common.exception.BindingResultHandler;

@Log4j2
@RestController
@RequestMapping("campuses")
@RequiredArgsConstructor
public class CampusController {

    private final CampusService campusService;
    private final CourseService courseService;

    @GetMapping
    public ResponseEntity<List<CampusResponse>> getCampuses() {
        List<CampusResponse> campuses = campusService.findAll();
        return ResponseEntity.ok(campuses);
    }

    @GetMapping("{campusId}/courses")
    public ResponseEntity<List<CourseResponse>> getCourses(@PathVariable Long campusId) {
        List<CourseResponse> courseResponses = courseService.findAll(campusId);
        return ResponseEntity.ok(courseResponses);
    }

    // 매니저 권한: 캠퍼스 등록
    @PostMapping
    public ResponseEntity<Void> createCampus(
            @Valid @RequestBody CreateCampusRequest request, BindingResult bindingResult
    ) {
        validateCampusInput(bindingResult);
        campusService.createCampus(request);

        return ResponseEntity.noContent().build();
    }

    // 매니저 권한: 캠퍼스 상세
    @GetMapping("{campusId}")
    public ResponseEntity<CampusDetailResponse> getCampus(@PathVariable Long campusId) {
        CampusDetailResponse response = campusService.getCampus(campusId);
        return ResponseEntity.ok().body(response);
    }

    // 매니저 권한: 캠퍼스 수정
    @PutMapping("{campusId}")
    public ResponseEntity<Void> updateCampus(
            @Valid @RequestBody UpdateCampusRequest request,
            BindingResult bindingResult,
            @PathVariable Long campusId
    ) {
        validateCampusInput(bindingResult);

        campusService.updateCampus(request, campusId);

        return ResponseEntity.noContent().build();
    }

    // 매니저 권한: 캠퍼스 삭제
    @DeleteMapping("{campusId}")
    public ResponseEntity<Void> deleteCampus(@PathVariable Long campusId) {
        campusService.deleteCampus(campusId);

        return ResponseEntity.noContent().build();
    }

    // 매니저 권한: 과정 등록
    @PostMapping("courses")
    public ResponseEntity<Void> createCourse(
            @AuthPrincipal CustomPrincipal principal,
            @Valid @RequestBody CreateCourseRequest request,
            BindingResult bindingResult
    ) {
        validateCourseInput(bindingResult);

        courseService.createCourse(principal, request);
        return ResponseEntity.noContent().build();
    }


    // 매니저 권한: 매니저 관리 강의 리스트
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("manager-courses")
    public ResponseEntity<List<CourseResponse>> getManagerCourses(
            @AuthPrincipal CustomPrincipal principal
    ) {
        List<CourseResponse> response = courseService.findManagerCourseAll(principal);

        return ResponseEntity.ok().body(response);
    }

    // 매니저 권한: 과정 상세 리스트
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("courses-extended")
    public ResponseEntity<PageResponse<ExtendedCourseResponse>> getDetailCourses(
            @AuthPrincipal CustomPrincipal principal,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String status,
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
        // 정렬                              필터링
        // 1. 기본: 개강일[최신날짜->과거]   1. 기본: 전부(page-0, size-10)
        // 2. 개강일[과거->최신날짜]        2. 개강(진행중)
        // 3. 사전순                      3. 종강
        // 4. 사전역순                    4. 개강 예정
        // 5. 생성일[최신날짜->과거]
        // 6. 생성일[과거->최신날짜]
        PageResponse<ExtendedCourseResponse> response = courseService.getCourseList(principal,
                pageable,
                name,
                status);

        return ResponseEntity.ok().body(response);
    }

    // 매니저 권한: 과정 상세
    @GetMapping("courses/{courseId}")
    public ResponseEntity<CourseDetailResponse> getCourse(
            @PathVariable Long courseId) {
        CourseDetailResponse response = courseService.getCourseDetail(courseId);
        return ResponseEntity.ok().body(response);
    }

    // 매니저 권한: 과정 수정
    @PutMapping("courses/{courseId}")
    public ResponseEntity<Void> updateCourse(
            @AuthPrincipal CustomPrincipal principal,
            @PathVariable Long courseId,
            @Valid @RequestBody UpdateCourseRequest request,
            BindingResult bindingResult
    ) {
        validateCourseInput(bindingResult);

        courseService.updateCourse(principal, courseId, request);
        return ResponseEntity.noContent().build();
    }

    // 매니저 권한: 과정 삭제
    @DeleteMapping("courses/{courseId}")
    public ResponseEntity<Void> deleteCourse(
            @AuthPrincipal CustomPrincipal principal,
            @PathVariable Long courseId
    ) {
        courseService.deleteCourse(principal, courseId);
        return ResponseEntity.noContent().build();
    }

    private void validateCampusInput(BindingResult bindingResult) {
        BindingResultHandler.handle(bindingResult, List.of(
                CampusErrorCode.REQUIRED_NAME,
                CampusErrorCode.INVALID_NAME_SIZE,
                CampusErrorCode.REQUIRED_ADDRESS,
                CampusErrorCode.INVALID_ADDRESS_SIZE
        ));
    }

    private void validateCourseInput(BindingResult bindingResult) {
        BindingResultHandler.handle(bindingResult, List.of(
                CourseErrorCode.REQUIRED_NAME,
                CourseErrorCode.INVALID_NAME_SIZE,

                CourseErrorCode.REQUIRED_CLASS_NUMBER,
                CourseErrorCode.INVALID_CLASS_NUMBER_SIZE,

                CourseErrorCode.REQUIRED_INSTRUCTOR_NAME,
                CourseErrorCode.INVALID_INSTRUCTOR_NAME_PATTERN,
                CourseErrorCode.INVALID_INSTRUCTOR_NAME_SIZE,

                CourseErrorCode.REQUIRED_START_DATE,
                CourseErrorCode.REQUIRED_END_DATE
        ));
    }
}
