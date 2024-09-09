package sesac.server.campus.service;

import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import sesac.server.campus.dto.request.CreateCampusRequest;
import sesac.server.campus.dto.request.UpdateCampusRequest;
import sesac.server.campus.dto.response.CampusDetailResponse;
import sesac.server.campus.dto.response.CampusResponse;
import sesac.server.campus.entity.Campus;
import sesac.server.campus.exception.CampusErrorCode;
import sesac.server.campus.repository.CampusRepository;
import sesac.server.common.exception.BaseException;

@Service
@Transactional
@Log4j2
@RequiredArgsConstructor
@EnableAsync
public class CampusService {

    private final CampusRepository campusRepository;
    private final CampusUpdateValidationService campusUpdateValidationService;

    public List<CampusResponse> findAll() {
        List<Campus> list = campusRepository.findAll();

        List<CampusResponse> response =
                list.stream().map(r -> campusToResponse(r)).toList();

        return response;
    }

    private CampusResponse campusToResponse(Campus campus) {
        return new CampusResponse(campus.getId(), campus.getName());
    }

    public void createCampus(CreateCampusRequest request) {
        Campus campus;
        campus = Campus.builder()
                .name(request.name())
                .address(request.address())
                .build();
        campusRepository.save(campus);
    }

    public CampusDetailResponse getCampus(Long campusId) {
        Campus campus = getCampusEntity(campusId);

        return CampusDetailResponse.from(campus);
    }

    public void deleteCampus(Long campusId) {
        Campus campus = getCampusEntity(campusId);
        campusRepository.delete(campus);
    }

    public void updateCampus(UpdateCampusRequest request, Long campusId) {
        Campus campus = getCampusEntity(campusId);

        // 유효성 검사 및 업데이트할 request get.
        UpdateCampusRequest validatedRequest = campusUpdateValidationService.validateAndPrepareUpdate(
                request, campus);

        campus.updateCampus(validatedRequest.name(), validatedRequest.address());

        campusRepository.save(campus);
    }

    private Campus getCampusEntity(Long campusId) {
        return campusRepository.findById(campusId).orElseThrow(
                () -> new BaseException(CampusErrorCode.NO_CAMPUS));
    }
}
