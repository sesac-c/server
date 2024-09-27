package sesac.server.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import sesac.server.auth.dto.CustomPrincipal;
import sesac.server.user.dto.request.NicknameCheckRequest;
import sesac.server.user.dto.response.ProfileResponse;
import sesac.server.user.dto.response.StudentProfileFormResponse;
import sesac.server.user.repository.ManagerRepository;
import sesac.server.user.repository.StudentRepository;
import sesac.server.user.repository.UserRepository;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final ManagerRepository managerRepository;

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
}
