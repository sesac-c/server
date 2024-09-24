package sesac.server.group.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sesac.server.auth.dto.AuthPrincipal;
import sesac.server.auth.dto.CustomPrincipal;
import sesac.server.common.exception.BindingResultHandler;
import sesac.server.group.dto.request.CreateRestaurantRequest;
import sesac.server.group.dto.response.RestaurantListForManagerResponse;
import sesac.server.group.dto.response.RestaurantListResponse;
import sesac.server.group.entity.GroupType;
import sesac.server.group.exception.RestaurantErrorCode;
import sesac.server.group.service.RestaurantService;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("restaurants")            // groupType - CAMPUS, RUNNINGMATE
public class RestaurantController {

    private final RestaurantService restaurantService;

    @GetMapping("{groupType}")
    public ResponseEntity<List<RestaurantListResponse>> getRestaurantList(
            @AuthPrincipal CustomPrincipal principal, @PathVariable GroupType groupType
    ) {
        List<RestaurantListResponse> response = restaurantService.getRestaurantList(
                principal, groupType);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("{groupType}/{restaurantId}")
    public ResponseEntity<Void> getRestaurant(
            @PathVariable GroupType groupType, @PathVariable String restaurantId
    ) {
        return null;
    }

    @GetMapping("{groupType}/{restaurantId}/menus")
    public ResponseEntity<Void> getRestaurantMenus(
            @PathVariable GroupType groupType, @PathVariable String restaurantId
    ) {
        return null;
    }


    // 매니저 권한: 전체 음식점 리스트 조회
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping
    public ResponseEntity<List<RestaurantListForManagerResponse>> getRestaurantListForManager(
            @AuthPrincipal CustomPrincipal principal, @Param("type") GroupType type
    ) {
        // 필터링: type(GroupType)
        List<RestaurantListForManagerResponse> response = restaurantService.getRestaurantListForManager(
                principal, type);
        return ResponseEntity.ok().body(response);
    }


    // 매니저 권한: 음식점 등록
    @PostMapping("{groupType}")
    public ResponseEntity<Void> createRestaurant(
            @Valid @RequestBody CreateRestaurantRequest request,
            BindingResult bindingResult,
            @AuthPrincipal CustomPrincipal principal,
            @PathVariable GroupType groupType
    ) {
        validateRestaurantInput(bindingResult);
        restaurantService.createRestaurant(principal, groupType, request);
        return ResponseEntity.noContent().build();
    }

    // 매니저 권한: 음식점 수정
    @PutMapping("{groupType}/{restaurantId}")
    public ResponseEntity<Void> updateRestaurant(
            @PathVariable GroupType groupType, @PathVariable Long restaurantId
    ) {
        return null;
    }

    // 매니저 권한: 음식점 삭제
    @DeleteMapping("{groupType}/{restaurantId}")
    public ResponseEntity<Void> deleteRestaurant(
            @PathVariable GroupType groupType, @PathVariable Long restaurantId
    ) {
        return null;
    }

    // 매니저 권한: 음식점 메뉴 등록
    @PostMapping("{groupType}/{restaurantId}/menus")
    public ResponseEntity<Void> createRestaurantMenu(
            @PathVariable GroupType groupType, @PathVariable Long restaurantId
    ) {
        return null;
    }

    // 매니저 권한: 음식점 메뉴 수정
    @PutMapping("{groupType}/{restaurantId}/menus/{menuId}")
    public ResponseEntity<Void> updateRestaurantMenu(
            @PathVariable GroupType groupType,
            @PathVariable Long restaurantId,
            @PathVariable Long menuId
    ) {
        return null;
    }

    // 매니저 권한: 음식점 메뉴 삭제
    @DeleteMapping("{groupType}/{restaurantId}/menus/{menuId}")
    public ResponseEntity<Void> deleteRestaurantMenu(
            @PathVariable GroupType groupType,
            @PathVariable Long restaurantId,
            @PathVariable Long menuId
    ) {
        return null;
    }

    private void validateRestaurantInput(BindingResult bindingResult) {
        BindingResultHandler.handle(bindingResult, List.of(
                RestaurantErrorCode.REQUIRED_NAME,
                RestaurantErrorCode.INVALID_NAME_SIZE,

                RestaurantErrorCode.REQUIRED_CATEGORY,
                RestaurantErrorCode.INVALID_CATEGORY_SIZE,

                RestaurantErrorCode.REQUIRED_ADDRESS,
                RestaurantErrorCode.INVALID_ADDRESS_SIZE,

                RestaurantErrorCode.REQUIRED_ADDRESS_ID,

                RestaurantErrorCode.REQUIRED_LATITUDE,
                RestaurantErrorCode.REQUIRED_LONGITUDE
        ));
    }
}
