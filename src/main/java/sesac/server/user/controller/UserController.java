package sesac.server.user.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sesac.server.user.dto.response.ManagerListResponse;
import sesac.server.user.dto.response.StudentListResponse;
import sesac.server.user.service.UserService;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("user")
public class UserController {

    private final UserService userService;

    @GetMapping("students")
    public ResponseEntity<List<StudentListResponse>> getStudentList(
            @Param("nickname") String nickname) {
        List<StudentListResponse> response = userService.getStudentList(nickname);

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("managers")
    public ResponseEntity<List<ManagerListResponse>> getManagerList() {
        List<ManagerListResponse> response = userService.getManagerList();

        return ResponseEntity.ok().body(response);
    }
}
