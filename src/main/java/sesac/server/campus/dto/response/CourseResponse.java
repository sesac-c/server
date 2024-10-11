package sesac.server.campus.dto.response;

import sesac.server.campus.entity.Course;

public record CourseResponse(Long id, String name, String classNumber) {

    private CourseResponse(Course course) {
        this(course.getId(), course.getName(), course.getClassNumber());
    }

    public static CourseResponse from(Course course) {
        return new CourseResponse(course);
    }
}
