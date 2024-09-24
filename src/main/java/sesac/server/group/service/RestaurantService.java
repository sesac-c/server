package sesac.server.group.service;

import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import sesac.server.auth.dto.CustomPrincipal;
import sesac.server.campus.entity.Campus;
import sesac.server.common.exception.BaseException;
import sesac.server.group.dto.request.CreateRestaurantRequest;
import sesac.server.group.dto.response.RestaurantDetailResponse;
import sesac.server.group.dto.response.RestaurantListForManagerResponse;
import sesac.server.group.dto.response.RestaurantListResponse;
import sesac.server.group.entity.GroupType;
import sesac.server.group.entity.Restaurant;
import sesac.server.group.exception.RestaurantErrorCode;
import sesac.server.group.repository.RestaurantRepository;
import sesac.server.user.exception.UserErrorCode;
import sesac.server.user.repository.ManagerRepository;
import sesac.server.user.repository.StudentRepository;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final ManagerRepository managerRepository;
    private final StudentRepository studentRepository;

    public void createRestaurant(CustomPrincipal principal, GroupType groupType,
            CreateRestaurantRequest request) {
        Campus campus = getManagerCampus(principal.id());

        Restaurant restaurant = request.toEntity(campus, groupType);
        restaurantRepository.save(restaurant);
    }

    public List<RestaurantListForManagerResponse> getRestaurantListForManager(
            CustomPrincipal principal,
            GroupType type) {
        Campus campus = getManagerCampus(principal.id());

        List<Restaurant> restaurants = (type == null)
                ? restaurantRepository.findByCampus(campus)
                : restaurantRepository.findByCampusAndType(campus, type);

        return restaurants.stream()
                .map(RestaurantListForManagerResponse::from)
                .toList();
    }

    public List<RestaurantListResponse> getRestaurantList(
            CustomPrincipal principal,
            GroupType type) {
        Campus campus = getStudentCampus(principal.id());

        List<Restaurant> restaurants = restaurantRepository.findByCampusAndType(campus, type);

        return restaurants.stream()
                .map(RestaurantListResponse::from)
                .toList();
    }

    public RestaurantDetailResponse getRestaurant(CustomPrincipal principal,
            GroupType type,
            Long restaurantId) {
        Long userId = principal.id();
        Campus userCampus = "STUDENT".equals(principal.role()) ? getStudentCampus(userId)
                : getManagerCampus(userId);

        Restaurant restaurant = restaurantRepository.findByIdAndTypeAndCampus(restaurantId, type,
                userCampus);

        if (restaurant == null) {
            throw new BaseException(RestaurantErrorCode.NOT_FOUND_RESTAURANT);
        }

        return RestaurantDetailResponse.from(restaurant);
    }

    private Campus getManagerCampus(Long managerId) {
        return managerRepository.findById(managerId).orElseThrow(
                () -> new BaseException(UserErrorCode.NO_USER)
        ).getCampus();
    }

    private Campus getStudentCampus(Long studentId) {
        return studentRepository.findById(studentId).orElseThrow(
                () -> new BaseException(UserErrorCode.NO_USER)
        ).getFirstCourse().getCampus();
    }
}
