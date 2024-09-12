package sesac.server.common;

import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import sesac.server.campus.entity.Campus;
import sesac.server.campus.entity.Course;
import sesac.server.user.entity.Manager;
import sesac.server.user.entity.Student;
import sesac.server.user.entity.User;
import sesac.server.user.entity.UserRole;

public class Fixture {

    public static EntityManager em;

    public static Campus createCampus(String campusName) {
        Campus campus = Campus.builder()
                .name(campusName)
                .address(campusName)
                .build();
        em.persist(campus);
        em.flush();
        em.clear();

        return campus;
    }

    public static Course createCourse(String courseName, Campus campus) {
        LocalDate now = LocalDate.now();
        Course course = Course.builder()
                .campus(campus)
                .name(courseName)
                .classNumber(courseName)
                .instructorName(courseName)
                .startDate(now)
                .endDate(now.plusMonths(1))
                .build();

        em.persist(course);
        em.flush();
        em.clear();
        return course;
    }

    public static Manager createManager(String name, Campus campus) {
        User user = User.builder()
                .email(name)
                .role(UserRole.MANAGER)
                .password("1234")
                .build();

        Manager manager = Manager.builder()
                .user(user)
                .campus(campus)
                .build();

        em.persist(user);
        em.persist(manager);
        em.flush();
        em.clear();

        return manager;
    }

    public static Student createStudent(String name, Course course, int statusCode) {
        User user = User.builder()
                .email(name)
                .role(UserRole.STUDENT)
                .password("1234")
                .build();

        Student student = Student.builder()
                .user(user)
                .name(name)
                .birthDate(LocalDate.parse("19990101",
                        DateTimeFormatter.ofPattern("yyyyMMdd")))
                .firstCourse(course)
                .gender('M')
                .nickname(name)
                .statusCode(statusCode)
                .build();

        em.persist(user);
        em.persist(student);
        em.flush();
        em.clear();

        return student;
    }
}
