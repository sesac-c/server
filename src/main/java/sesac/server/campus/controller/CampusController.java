package sesac.server.campus.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sesac.server.campus.dto.CampusResponse;
import sesac.server.campus.service.CampusService;

@Log4j2
@RestController
@RequestMapping("campuses")
@RequiredArgsConstructor
public class CampusController {

    private final CampusService campusService;

    @GetMapping
    public ResponseEntity<List<CampusResponse>> getCampuses() {
        List<CampusResponse> campuses = campusService.findAll();
        return ResponseEntity.ok(campuses);
    }
}
