package sesac.server.campus.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sesac.server.campus.dto.CampusResponse;
import sesac.server.campus.dto.CourseResponse;
import sesac.server.campus.service.CampusService;
import sesac.server.campus.service.CourseService;

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

    @GetMapping("/{campusId}/courses")
    public ResponseEntity<List<CourseResponse>> getCourses(@PathVariable Long campusId) {
        List<CourseResponse> courseResponses = courseService.findAll(campusId);
        return ResponseEntity.ok(courseResponses);
    }

    // 매니저 권한: 캠퍼스 등록
    @PostMapping
    public ResponseEntity<Void> createCampus() {
        return null;
    }

    // 매니저 권한: 캠퍼스 상세
    @GetMapping("/{campusId}")
    public ResponseEntity<Void> getCampus(@PathVariable Long campusId) {
        return null;
    }

    // 매니저 권한: 캠퍼스 수정
    @PutMapping("/{campusId}")
    public ResponseEntity<Void> updateCampus(@PathVariable Long campusId) {
        return null;
    }

    // 매니저 권한: 캠퍼스 삭제
    @DeleteMapping("/{campusId}")
    public ResponseEntity<Void> deleteCampus(@PathVariable Long campusId) {
        return null;
    }

    // 매니저 권한: 과정 등록
    @PostMapping("/{campusId}/courses")
    public ResponseEntity<Void> createCourse(@PathVariable Long campusId,
            @RequestBody CourseResponse courseResponse) {
        return null;
    }

    // 매니저 권한: 과정 상세
    @GetMapping("/{campusId}/courses/{courseId}")
    public ResponseEntity<Void> getCourse(@PathVariable Long campusId,
            @PathVariable Long courseId) {
        return null;
    }

    // 매니저 권한: 과정 수정
    @PutMapping("/{campusId}/courses/{courseId}")
    public ResponseEntity<Void> updateCourse(@PathVariable Long campusId,
            @PathVariable Long courseId) {
        return null;
    }

    // 매니저 권한: 과정 삭제
    @DeleteMapping("/{campusId}/courses/{courseId}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long campusId,
            @PathVariable Long courseId) {
        return null;
    }
}
