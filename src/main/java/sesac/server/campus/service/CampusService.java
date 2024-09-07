package sesac.server.campus.service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import sesac.server.campus.dto.request.CampusRequest;
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

    public List<CampusResponse> findAll() {
        List<Campus> list = campusRepository.findAll();

        List<CampusResponse> response =
                list.stream().map(r -> campusToResponse(r)).toList();

        return response;
    }

    private CampusResponse campusToResponse(Campus campus) {
        return new CampusResponse(campus.getId(), campus.getName());
    }

    public void createCampus(CampusRequest request) {
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

    public void updateCampus(CampusRequest request, Long campusId) {
        Campus campus = getCampusEntity(campusId);

        String oldName = campus.getName();
        String oldAddress = campus.getAddress();
        boolean isNameChanged = !oldName.equals(request.name());
        boolean isAddressChanged = !oldAddress.equals(request.address());

        if (!isNameChanged && !isAddressChanged) {
            throw new BaseException(CampusErrorCode.SAME_AS_PREVIOUS);
        }

        campus.updateCampus(
                isNameChanged ? request.name() : null,
                isAddressChanged ? request.address() : null
        );

        campusRepository.save(campus);

        // update된 항목만 로그를 찍기 위해서(비동기)
        logAsync(() -> buildUpdateLogMessage(campusId, oldName, oldAddress,
                request.name(), request.address(),
                isNameChanged, isAddressChanged));
    }

    private Campus getCampusEntity(Long campusId) {
        return campusRepository.findById(campusId).orElseThrow(
                () -> new BaseException(CampusErrorCode.NO_CAMPUS));
    }

    private static void logAsync(Supplier<String> messageSupplier) {
        CompletableFuture.supplyAsync(messageSupplier)
                .thenAccept(log::info)
                .exceptionally(e -> {
                    log.error("로그메시지 빌드 중 에러: ", e);
                    return null;
                });
    }

    private String buildUpdateLogMessage(Long campusId, String oldName, String oldAddress,
            String newName, String newAddress,
            boolean isNameChanged, boolean isAddressChanged) {
        StringBuilder logMessage = new StringBuilder("<ID: " + campusId + "> Campus 업데이트,");

        if (isNameChanged) {
            logMessage.append(" [캠퍼스명: '").append(oldName).append("' -> '").append(newName)
                    .append("']");
        }
        if (isAddressChanged) {
            logMessage.append(" [주소: '").append(oldAddress).append("' -> '").append(newAddress)
                    .append("']");
        }
        return logMessage.toString();
    }
}
