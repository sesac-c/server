package sesac.server.campus.service;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import sesac.server.campus.dto.request.UpdateCourseRequest;
import sesac.server.campus.entity.Course;
import sesac.server.common.exception.BaseException;
import sesac.server.common.exception.GlobalErrorCode;
import sesac.server.common.service.UpdateValidationService;

@Service
public class CourseUpdateValidationService extends
        UpdateValidationService<UpdateCourseRequest, Course> {

    @Override
    protected void validateNotAllFieldsNull(UpdateCourseRequest request) {
        if (request.name() == null && request.classNumber() == null
                && request.instructorName() == null && request.startDate() == null
                && request.endDate() == null && request.newCampusId() == null) {
            throw new BaseException(GlobalErrorCode.AT_LEAST_ONE_FIELD_REQUIRED);
        }
    }

    @Override
    protected Map<String, Boolean> detectChanges(UpdateCourseRequest request, Course course) {
        Map<String, Boolean> changedFields = new HashMap<>();
        changedFields.put("name", isFieldChanged(request.name(), course.getName()));
        changedFields.put("classNumber",
                isFieldChanged(request.classNumber(), course.getClassNumber()));
        changedFields.put("instructorName",
                isFieldChanged(request.instructorName(), course.getInstructorName()));
        changedFields.put("startDate", isFieldChanged(request.startDate(), course.getStartDate()));
        changedFields.put("endDate", isFieldChanged(request.endDate(), course.getEndDate()));
        changedFields.put("campusId", isFieldChanged(request.newCampusId(),
                course.getCampus() != null ? course.getCampus().getId() : null));
        return changedFields;
    }

    @Override
    protected UpdateCourseRequest createUpdatedRequest(UpdateCourseRequest originalRequest,
            Map<String, Boolean> changedFields) {
        return new UpdateCourseRequest(
                changedFields.get("name") ? originalRequest.name() : null,
                changedFields.get("classNumber") ? originalRequest.classNumber() : null,
                changedFields.get("instructorName") ? originalRequest.instructorName() : null,
                changedFields.get("startDate") ? originalRequest.startDate() : null,
                changedFields.get("endDate") ? originalRequest.endDate() : null,
                changedFields.get("campusId") ? originalRequest.newCampusId() : null
        );
    }

    @Override
    protected void logUpdateAsync(Course course, UpdateCourseRequest updatedRequest,
            Map<String, Boolean> changedFields) {
        logAsync(() -> buildUpdateLogMessage(course, updatedRequest, changedFields));
    }

    @Override
    protected String buildUpdateLogMessage(Course course, UpdateCourseRequest updatedRequest,
            Map<String, Boolean> changedFields) {
        StringBuilder logMessage = new StringBuilder("<ID: " + course.getId() + "> Course 업데이트,");

        if (changedFields.get("name")) {
            logMessage.append(" [과정명: '").append(course.getName()).append("' -> '")
                    .append(updatedRequest.name()).append("']");
        }
        if (changedFields.get("classNumber")) {
            logMessage.append(" [강의실: '").append(course.getClassNumber()).append("' -> '")
                    .append(updatedRequest.classNumber()).append("']");
        }
        if (changedFields.get("instructorName")) {
            logMessage.append(" [강사명: '").append(course.getInstructorName()).append("' -> '")
                    .append(updatedRequest.instructorName()).append("']");
        }
        if (changedFields.get("startDate")) {
            logMessage.append(" [시작일: '").append(course.getStartDate()).append("' -> '")
                    .append(updatedRequest.startDate()).append("']");
        }
        if (changedFields.get("endDate")) {
            logMessage.append(" [종료일: '").append(course.getEndDate()).append("' -> '")
                    .append(updatedRequest.endDate()).append("']");
        }
        if (changedFields.get("campusId")) {
            logMessage.append(" [캠퍼스ID: '")
                    .append(course.getCampus() != null ? course.getCampus().getId() : "null")
                    .append("' -> '")
                    .append(updatedRequest.newCampusId()).append("']");
        }
        return logMessage.toString();
    }

    // LocalDate를 비교하기 위한 추가 메서드
    protected boolean isFieldChanged(Object newValue, Object oldValue) {
        if (newValue == null) {
            return false;
        }
        return !newValue.equals(oldValue);
    }
}