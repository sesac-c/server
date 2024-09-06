package sesac.server.group.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("members")
public class MemberController {

    @GetMapping("course/{courseId}")
    public ResponseEntity<Void> getCourseMembers(@PathVariable("courseId") Long courseId) {
        return null;
    }

    @GetMapping("runningmate/{runningmateId}")
    public ResponseEntity<Void> getRunningmateMembers(
            @PathVariable("runningmateId") Long runningmateId) {
        return null;
    }
}
