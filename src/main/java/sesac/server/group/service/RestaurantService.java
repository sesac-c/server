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
import sesac.server.common.entity.HasCampus;
import sesac.server.common.exception.BaseException;
import sesac.server.common.exception.GlobalErrorCode;
import sesac.server.group.dto.request.CreateMenuRequest;
import sesac.server.group.dto.request.CreateRestaurantRequest;
import sesac.server.group.dto.request.UpdateMenuRequest;
import sesac.server.group.dto.request.UpdateRestaurantRequest;
import sesac.server.group.dto.response.MenuResponse;
import sesac.server.group.dto.response.RestaurantDetailResponse;
import sesac.server.group.dto.response.RestaurantListForManagerResponse;
import sesac.server.group.dto.response.RestaurantListResponse;
import sesac.server.group.entity.GroupType;
import sesac.server.group.entity.Menu;
import sesac.server.group.entity.Restaurant;
import sesac.server.group.exception.MenuErrorCode;
import sesac.server.group.exception.RestaurantErrorCode;
import sesac.server.group.repository.MenuRepository;
import sesac.server.group.repository.RestaurantRepository;
import sesac.server.user.service.UserService;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final MenuRepository menuRepository;
    private final UserService userService;

    // Restaurant
    public void createRestaurant(CustomPrincipal principal, GroupType groupType,
            CreateRestaurantRequest request) {
        Campus campus = userService.getUserCampus(principal);

        // TODO: validateAddressInfo

        Restaurant restaurant = request.toEntity(campus, groupType);

        restaurantRepository.save(restaurant);
    }

    public List<RestaurantListForManagerResponse> getRestaurantListForManager(
            CustomPrincipal principal,
            String name) {
        Campus campus = userService.getUserCampus(principal);

        return getRestaurantList(campus, name, RestaurantListForManagerResponse::from);
    }

    public List<RestaurantListResponse> getRestaurantList(
            CustomPrincipal principal,
            GroupType type) {
        Campus campus = userService.getUserCampus(principal);

        return restaurantRepository.findByCampusAndTypeOrderByIdDesc(campus, type).stream()
                .map(RestaurantListResponse::from).toList();
    }

    public RestaurantDetailResponse getRestaurant(CustomPrincipal principal,
            GroupType type,
            Long restaurantId) {
        Restaurant restaurant = findRestaurantByIdAndType(restaurantId, type);
        Campus userCampus = userService.getUserCampus(principal);
        validateUserPermission(restaurant, userCampus);

        return RestaurantDetailResponse.from(restaurant);
    }

    public void updateRestaurant(CustomPrincipal principal, GroupType type, Long restaurantId,
            UpdateRestaurantRequest request) {
        Restaurant restaurant = findRestaurantByIdAndType(restaurantId, type);
        Campus managerCampus = userService.getUserCampus(principal);

        validateUserPermission(restaurant, managerCampus);
        validateAddressUpdate(request);

        restaurant.update(request);
    }

    public void deleteRestaurant(CustomPrincipal principal, GroupType type, Long restaurantId) {
        Restaurant restaurant = findRestaurantByIdAndType(restaurantId, type);
        Campus managerCampus = userService.getUserCampus(principal);
        validateUserPermission(restaurant, managerCampus);

        restaurantRepository.delete(restaurant);
    }

    // Menu
    public void createRestaurantMenu(CustomPrincipal principal, GroupType type,
            Long restaurantId, CreateMenuRequest request) {
        Restaurant restaurant = findRestaurantByIdAndType(restaurantId, type);
        Campus managerCampus = userService.getUserCampus(principal);
        validateUserPermission(restaurant, managerCampus);

        // 메뉴 생성
        Menu menu = request.toEntity(restaurant);
        menuRepository.save(menu);
    }

    public List<MenuResponse> getRestaurantMenu(GroupType type,
            Long restaurantId) {
        List<Menu> menu = menuRepository.findByRestaurantIdAndType(restaurantId, type);
        return menu.stream().map(MenuResponse::from).toList();
    }

    public void updateRestaurantMenu(CustomPrincipal principal, GroupType type, Long restaurantId,
            Long menuId, UpdateMenuRequest request) {
        Menu menu = menuRepository.findByIdAndRestaurantIdAndType(menuId, restaurantId, type)
                .orElseThrow(
                        () -> new BaseException(MenuErrorCode.NOT_FOUND_MENU)
                );
        Campus managerCampus = userService.getUserCampus(principal);
        validateUserPermission(menu, managerCampus);

        menu.update(request);
    }

    public void deleteRestaurantMenu(CustomPrincipal principal, GroupType type,
            Long restaurantId, Long menuId) {
        Menu menu = menuRepository.findByIdAndRestaurantIdAndType(menuId, restaurantId, type)
                .orElseThrow(
                        () -> new BaseException(MenuErrorCode.NOT_FOUND_MENU)
                );
        Campus managerCampus = userService.getUserCampus(principal);
        validateUserPermission(menu, managerCampus);

        menuRepository.delete(menu);
    }

    private <T> List<T> getRestaurantList(Campus campus, String name,
            Function<Restaurant, T> mapper) {
        List<Restaurant> restaurants = (name == null)
                ? restaurantRepository.findByCampusOrderByIdDesc(campus)
                : restaurantRepository.findByCampusAndNameContainingOrderByIdDesc(campus, name);

        return restaurants.stream().map(mapper).toList();
    }

    private Restaurant findRestaurantByIdAndType(Long restaurantId, GroupType type) {
        return restaurantRepository.findByIdAndType(restaurantId, type)
                .orElseThrow(() -> new BaseException(RestaurantErrorCode.NOT_FOUND_RESTAURANT));
    }

    private void validateUserPermission(HasCampus entity, Campus userCampus) {
        if (!entity.getCampus().equals(userCampus)) {
            throw new BaseException(GlobalErrorCode.NO_PERMISSIONS);
        }
    }

    private void validateAddressUpdate(UpdateRestaurantRequest request) {
        if (request.isAddressInfoChanged()) {
            if (!request.isAddressInfoComplete()) {
                throw new BaseException(RestaurantErrorCode.INCOMPLETE_ADDRESS_INFO);
            }
            // TODO: validateAddressInfo
        }
    }

    private boolean validateAddressInfo(Long addressId, String address, BigDecimal longitude,
            BigDecimal latitude) {
        // TODO: 주소, 주소 id, 위도/경도 유효성 검사 구현
        return true;
    }
}
