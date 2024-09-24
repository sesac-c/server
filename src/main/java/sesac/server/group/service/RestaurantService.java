package sesac.server.group.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import sesac.server.auth.dto.CustomPrincipal;
import sesac.server.campus.entity.Campus;
import sesac.server.campus.exception.CampusErrorCode;
import sesac.server.campus.repository.CampusRepository;
import sesac.server.common.exception.BaseException;
import sesac.server.group.dto.request.CreateRestaurantRequest;
import sesac.server.group.entity.GroupType;
import sesac.server.group.entity.Restaurant;
import sesac.server.group.repository.RestaurantRepository;
import sesac.server.user.entity.Manager;
import sesac.server.user.exception.UserErrorCode;
import sesac.server.user.repository.ManagerRepository;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final CampusRepository campusRepository;
    private final ManagerRepository managerRepository;

    public void createRestaurant(CustomPrincipal principal, GroupType groupType,
            CreateRestaurantRequest request) {
        Campus campus = ((Manager) getEntity("manager", principal.id())).getCampus();

        Restaurant restaurant = request.toEntity(campus, groupType);
        restaurantRepository.save(restaurant);
    }

    private Object getEntity(String entityType, Long id) {
        return switch (entityType) {
            case "campus" -> campusRepository.findById(id).orElseThrow(
                    () -> new BaseException(CampusErrorCode.NO_CAMPUS)
            );
            case "manager" -> managerRepository.findById(id).orElseThrow(
                    () -> new BaseException(UserErrorCode.NO_USER)
            );
            default -> null;
        };
    }
}
