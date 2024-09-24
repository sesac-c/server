package sesac.server.group.service;

import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import sesac.server.auth.dto.CustomPrincipal;
import sesac.server.campus.entity.Campus;
import sesac.server.common.exception.BaseException;
import sesac.server.common.exception.GlobalErrorCode;
import sesac.server.group.dto.request.CreateRestaurantRequest;
import sesac.server.group.dto.request.UpdateRestaurantRequest;
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

//        validateAddressInfo(request.addressId(), request.address(), request.longitude(),
//                request.latitude());

        Restaurant restaurant = request.toEntity(campus, groupType);

        restaurantRepository.save(restaurant);
    }

    public List<RestaurantListForManagerResponse> getRestaurantListForManager(
            CustomPrincipal principal,
            GroupType type) {
        Campus campus = getManagerCampus(principal.id());

        return getRestaurantList(campus, type, RestaurantListForManagerResponse::from);
    }

    public List<RestaurantListResponse> getRestaurantList(
            CustomPrincipal principal,
            GroupType type) {
        Campus campus = getStudentCampus(principal.id());

        return getRestaurantList(campus, type, RestaurantListResponse::from);
    }

    public RestaurantDetailResponse getRestaurant(CustomPrincipal principal,
            GroupType type,
            Long restaurantId) {
        Restaurant restaurant = findRestaurantByIdAndType(restaurantId, type);
        Campus userCampus = getUserCampus(principal);
        validateUserPermission(restaurant, userCampus);

        return RestaurantDetailResponse.from(restaurant);
    }

    public void updateRestaurant(CustomPrincipal principal, GroupType type, Long restaurantId,
            UpdateRestaurantRequest request) {
        Restaurant restaurant = findRestaurantByIdAndType(restaurantId, type);
        Campus managerCampus = getManagerCampus(principal.id());
        validateUserPermission(restaurant, managerCampus);

        validateAddressUpdate(request);

        restaurant.update(request);
    }

    public void deleteRestaurant(CustomPrincipal principal, GroupType type, Long restaurantId) {
        Restaurant restaurant = findRestaurantByIdAndType(restaurantId, type);
        Campus managerCampus = getManagerCampus(principal.id());
        validateUserPermission(restaurant, managerCampus);

        restaurantRepository.delete(restaurant);
    }

    private <T> List<T> getRestaurantList(Campus campus, GroupType type,
            Function<Restaurant, T> mapper) {
        List<Restaurant> restaurants = (type == null)
                ? restaurantRepository.findByCampus(campus)
                : restaurantRepository.findByCampusAndType(campus, type);

        return restaurants.stream().map(mapper).toList();
    }

    private Restaurant findRestaurantByIdAndType(Long restaurantId, GroupType type) {
        return restaurantRepository.findByIdAndType(restaurantId, type)
                .orElseThrow(() -> new BaseException(RestaurantErrorCode.NOT_FOUND_RESTAURANT));
    }

    private void validateUserPermission(Restaurant restaurant, Campus userCampus) {
        if (!restaurant.getCampus().equals(userCampus)) {
            throw new BaseException(GlobalErrorCode.NO_PERMISSIONS);
        }
    }

    private void validateAddressUpdate(UpdateRestaurantRequest request) {
        if (request.isAddressInfoChanged()) {
            if (!request.isAddressInfoComplete()) {
                throw new BaseException(RestaurantErrorCode.INCOMPLETE_ADDRESS_INFO);
            }
//            validateAddressInfo(request.addressId(), request.address(), request.longitude(),
//                    request.latitude());
        }
    }

    private boolean validateAddressInfo(Long addressId, String address, BigDecimal longitude,
            BigDecimal latitude) {
        // TODO: 주소, 주소 id, 위도/경도 유효성 검사 구현
        return true;
    }

    private Campus getUserCampus(CustomPrincipal principal) {
        return "STUDENT".equals(principal.role())
                ? getStudentCampus(principal.id())
                : getManagerCampus(principal.id());
    }

    private Campus getManagerCampus(Long managerId) {
        return managerRepository.findById(managerId)
                .orElseThrow(() -> new BaseException(UserErrorCode.NO_USER))
                .getCampus();
    }

    private Campus getStudentCampus(Long studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new BaseException(UserErrorCode.NO_USER))
                .getFirstCourse().getCampus();
    }
}
