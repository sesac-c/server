package sesac.server.group.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sesac.server.campus.entity.Course;
import sesac.server.campus.exception.CourseErrorCode;
import sesac.server.campus.repository.CourseRepository;
import sesac.server.common.dto.PageResponse;
import sesac.server.common.exception.BaseException;
import sesac.server.group.dto.request.CreateRunningMateRequest;
import sesac.server.group.dto.request.SearchRunningMateRequest;
import sesac.server.group.dto.request.UpdateRunningMateRequest;
import sesac.server.group.dto.response.RunningMateDetailResponse;
import sesac.server.group.dto.response.SearchRunningMateResponse;
import sesac.server.group.entity.RunningMate;
import sesac.server.group.exception.RunningMateErrorCode;
import sesac.server.group.repository.RepositoryRunningMate;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class RunningMateService {

    private final RepositoryRunningMate runningmateRepository;
    private final CourseRepository courseRepository;

    public PageResponse<SearchRunningMateResponse> getRunningmateList(Pageable pageable,
            SearchRunningMateRequest request) {
        Page<SearchRunningMateResponse> responses =
                runningmateRepository.runningMateSearch(pageable, request);
        return new PageResponse(responses);
    }

    public Long createRunningmate(CreateRunningMateRequest request) {
        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new BaseException(CourseErrorCode.NO_COURSE));

        RunningMate runningMate = request.toEntity(course);
        runningmateRepository.save(runningMate);
        return runningMate.getId();
    }

    public RunningMateDetailResponse getRunningmate(Long runningmateId) {
        RunningMate runningMate = runningmateRepository.findById(runningmateId)
                .orElseThrow(() -> new BaseException(RunningMateErrorCode.NO_RUNNING_MATE));

        return RunningMateDetailResponse.from(runningMate);
    }

    public Long updateRunningmate(Long runningmateId, UpdateRunningMateRequest request) {
        RunningMate runningMate = runningmateRepository.findById(runningmateId)
                .orElseThrow(() -> new BaseException(RunningMateErrorCode.NO_RUNNING_MATE));

        runningMate.update(request);
        runningmateRepository.save(runningMate);
        return runningMate.getId();
    }

    public void deleteRunningmate(Long runningmateId) {
        RunningMate runningMate = runningmateRepository.findById(runningmateId)
                .orElseThrow(() -> new BaseException(RunningMateErrorCode.NO_RUNNING_MATE));

        runningmateRepository.delete(runningMate);
    }

}
