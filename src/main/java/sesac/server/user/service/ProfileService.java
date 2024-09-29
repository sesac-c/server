package sesac.server.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sesac.server.auth.dto.CustomPrincipal;
import sesac.server.user.dto.request.NicknameCheckRequest;
import sesac.server.user.dto.request.UpdateProfileRequest;
import sesac.server.user.dto.response.ProfileResponse;
import sesac.server.user.dto.response.StudentProfileFormResponse;
import sesac.server.user.entity.Manager;
import sesac.server.user.entity.Student;
import sesac.server.user.repository.ManagerRepository;
import sesac.server.user.repository.StudentRepository;
import sesac.server.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class ProfileService extends CommonUserService {

    private final UserRepository userRepository;
    private final ManagerRepository managerRepository;
    private final StudentRepository studentRepository;

    private static final String DEFAULT_PROFILE_IMAGE = "default-profile.png";

    public ProfileResponse getProfile(CustomPrincipal principal, Long profileUserId) {
        return userRepository.getProfileResponse(profileUserId, principal);
    }

    public boolean isExistNickname(NicknameCheckRequest request) {
        return studentRepository.existsByNickname(request.nickname());
    }

    public String getManagerProfileForm(CustomPrincipal customPrincipal) {
        String profileImage = managerRepository.findProfileImageById(customPrincipal.id());
        if (profileImage == null) {
            profileImage = DEFAULT_PROFILE_IMAGE;
        }
        return profileImage;
    }

    public StudentProfileFormResponse getStudentProfileForm(CustomPrincipal customPrincipal) {
        return studentRepository.getStudentProfileFormResponse(customPrincipal);
    }

    public void updateProfile(CustomPrincipal principal, UpdateProfileRequest request) {
        String role = principal.role();
        Long id = principal.id();

        switch (role) {
            case "STUDENT" -> updateStudentProfile(id, request);
            case "MANAGER" -> updateManagerProfile(id, request);
            default -> throw new IllegalStateException("존재하지 않는 권한입니다: " + role);
        }
    }

    private void updateStudentProfile(Long id, UpdateProfileRequest request) {
        Student student = getUserOrThrowException(studentRepository, id);
        student.updateProfile(request);
        studentRepository.save(student);
    }

    private void updateManagerProfile(Long id, UpdateProfileRequest request) {
        Manager manager = getUserOrThrowException(managerRepository, id);
        manager.updateProfile(request);
        managerRepository.save(manager);
    }
}
