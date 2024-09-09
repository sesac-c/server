package sesac.server.campus.service;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import sesac.server.campus.dto.request.UpdateCampusRequest;
import sesac.server.campus.entity.Campus;
import sesac.server.common.exception.BaseException;
import sesac.server.common.exception.GlobalErrorCode;
import sesac.server.common.service.UpdateValidationService;

@Service
public class CampusUpdateValidationService extends
        UpdateValidationService<UpdateCampusRequest, Campus> {

    @Override
    protected void validateNotAllFieldsNull(UpdateCampusRequest request) {
        if (request.name() == null && request.address() == null) {
            throw new BaseException(GlobalErrorCode.AT_LEAST_ONE_FIELD_REQUIRED);
        }
    }

    @Override
    protected Map<String, Boolean> detectChanges(UpdateCampusRequest request, Campus campus) {
        Map<String, Boolean> changedFields = new HashMap<>();
        changedFields.put("name", isFieldChanged(request.name(), campus.getName()));
        changedFields.put("address", isFieldChanged(request.address(), campus.getAddress()));
        return changedFields;
    }

    @Override
    protected UpdateCampusRequest createUpdatedRequest(UpdateCampusRequest originalRequest,
            Map<String, Boolean> changedFields) {
        String newName = changedFields.get("name") ? originalRequest.name() : null;
        String newAddress = changedFields.get("address") ? originalRequest.address() : null;
        return new UpdateCampusRequest(newName, newAddress);
    }

    @Override
    protected void logUpdateAsync(Campus campus, UpdateCampusRequest updatedRequest,
            Map<String, Boolean> changedFields) {
        logAsync(() -> buildUpdateLogMessage(campus, updatedRequest, changedFields));
    }

    @Override
    protected String buildUpdateLogMessage(Campus campus, UpdateCampusRequest updatedRequest,
            Map<String, Boolean> changedFields) {
        StringBuilder logMessage = new StringBuilder("<ID: " + campus.getId() + "> Campus 업데이트,");

        if (changedFields.get("name")) {
            logMessage.append(" [캠퍼스명: '").append(campus.getName()).append("' -> '")
                    .append(updatedRequest.name()).append("']");
        }
        if (changedFields.get("address")) {
            logMessage.append(" [주소: '").append(campus.getAddress()).append("' -> '")
                    .append(updatedRequest.address()).append("']");
        }
        return logMessage.toString();
    }
}