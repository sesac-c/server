package sesac.server.group.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sesac.server.user.dto.response.StudentListResponse;
import sesac.server.user.service.UserService;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("members")
public class MemberController {

    private final UserService userService;

    @GetMapping("course/{courseId}")
    public ResponseEntity<List<StudentListResponse>> getCourseMembers(
            @PathVariable("courseId") Long courseId) {
        List<StudentListResponse> students = userService.getCourseUsers(courseId);

        return ResponseEntity.ok(students);
    }

    @GetMapping("runningmate/{runningmateId}")
    public ResponseEntity<List<StudentListResponse>> getRunningmateMembers(
            @PathVariable("runningmateId") Long runningmateId) {
        List<StudentListResponse> students = userService.getRunningMateUsers(runningmateId);

        return ResponseEntity.ok(students);
    }
}
