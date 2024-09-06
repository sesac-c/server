package sesac.server.group.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sesac.server.group.entity.GroupType;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("restaurants/{groupType}")            // restaurantType - CAMPUS, RUNNINGMATE
public class RestaurantController {

    @GetMapping
    public ResponseEntity<Void> getRestaurantList(@PathVariable GroupType restaurantType) {
        return null;
    }

    @GetMapping("{restaurantId}")
    public ResponseEntity<Void> getRestaurant(
            @PathVariable GroupType restaurantType, @PathVariable String restaurantId
    ) {
        return null;
    }

    @GetMapping("{restaurantId}/menus")
    public ResponseEntity<Void> getRestaurantMenus(
            @PathVariable GroupType restaurantType, @PathVariable String restaurantId
    ) {
        return null;
    }

    // 매니저 권한: 음식점 등록
    @PostMapping
    public ResponseEntity<Void> createRestaurant(@PathVariable GroupType restaurantType) {
        return null;
    }

    // 매니저 권한: 음식점 수정
    @PutMapping("{restaurantId}")
    public ResponseEntity<Void> updateRestaurant(
            @PathVariable GroupType restaurantType, @PathVariable Long restaurantId
    ) {
        return null;
    }

    // 매니저 권한: 음식점 삭제
    @DeleteMapping("{restaurantId}")
    public ResponseEntity<Void> deleteRestaurant(
            @PathVariable GroupType restaurantType, @PathVariable Long restaurantId
    ) {
        return null;
    }

    // 매니저 권한: 음식점 메뉴 등록
    @PostMapping("{restaurantId}/menus")
    public ResponseEntity<Void> createRestaurantMenu(
            @PathVariable GroupType restaurantType, @PathVariable Long restaurantId
    ) {
        return null;
    }

    // 매니저 권한: 음식점 메뉴 수정
    @PutMapping("{restaurantId}/menus/{menuId}")
    public ResponseEntity<Void> updateRestaurantMenu(
            @PathVariable GroupType restaurantType,
            @PathVariable Long restaurantId,
            @PathVariable Long menuId
    ) {
        return null;
    }

    // 매니저 권한: 음식점 메뉴 삭제
    @DeleteMapping("{restaurantId}/menus/{menuId}")
    public ResponseEntity<Void> deleteRestaurantMenu(
            @PathVariable GroupType restaurantType,
            @PathVariable Long restaurantId,
            @PathVariable Long menuId
    ) {
        return null;
    }
}
