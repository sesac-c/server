package sesac.server.campus.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sesac.server.campus.dto.request.CourseRequest;
import sesac.server.campus.dto.response.CourseResponse;
import sesac.server.campus.dto.response.ExtendedCourseResponse;
import sesac.server.campus.entity.Campus;
import sesac.server.campus.entity.Course;
import sesac.server.campus.exception.CampusErrorCode;
import sesac.server.campus.repository.CampusRepository;
import sesac.server.campus.repository.CourseRepository;
import sesac.server.common.dto.PageResponse;
import sesac.server.common.exception.BaseException;

@Service
@Log4j2
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final CampusRepository campusRepository;

    public List<CourseResponse> findAll(Long campusId) {
        List<Course> list = courseRepository.findByCampusId(campusId);

        List<CourseResponse> response =
                list.stream().map(r -> campusToResponse(r)).toList();

        return response;
    }

    public PageResponse<ExtendedCourseResponse> getCourseList(
            Pageable pageable,
            Long campusId,
            String status
    ) {
        Page<ExtendedCourseResponse> courses = courseRepository.searchCourse(pageable, campusId,
                status);

        return new PageResponse<>(courses);
    }

    public void createCourse(Long campusId, CourseRequest request) {
        Campus campus = campusRepository.findById(campusId).orElseThrow(
                () -> new BaseException(CampusErrorCode.NO_CAMPUS)
        );

        Course course = Course.builder()
                .name(request.name())
                .classNumber(request.classNumber())
                .instructorName(request.instructorName())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .campus(campus)
                .build();

        courseRepository.save(course);
    }

    private CourseResponse campusToResponse(Course course) {
        return new CourseResponse(course.getId(), course.getName(), course.getClassNumber());
    }
}
