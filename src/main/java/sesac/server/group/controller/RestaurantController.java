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
import sesac.server.group.entity.RestaurantType;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("restaurants/{restaurantType}")            // restaurantType - CAMPUS, RUNNINGMATE
public class RestaurantController {

    @GetMapping
    public ResponseEntity<Void> getRestaurantList(@PathVariable RestaurantType restaurantType) {
        return null;
    }

    @GetMapping("{restaurantId}")
    public ResponseEntity<Void> getRestaurant(
            @PathVariable RestaurantType restaurantType, @PathVariable String restaurantId
    ) {
        return null;
    }

    @GetMapping("{restaurantId}/menus")
    public ResponseEntity<Void> getRestaurantMenus(
            @PathVariable RestaurantType restaurantType, @PathVariable String restaurantId
    ) {
        return null;
    }

    // 매니저 권한: 음식점 등록
    @PostMapping
    public ResponseEntity<Void> createRestaurant(@PathVariable RestaurantType restaurantType) {
        return null;
    }

    // 매니저 권한: 음식점 수정
    @PutMapping("{restaurantId}")
    public ResponseEntity<Void> updateRestaurant(
            @PathVariable RestaurantType restaurantType, @PathVariable Long restaurantId
    ) {
        return null;
    }

    // 매니저 권한: 음식점 삭제
    @DeleteMapping("{restaurantId}")
    public ResponseEntity<Void> deleteRestaurant(
            @PathVariable RestaurantType restaurantType, @PathVariable Long restaurantId
    ) {
        return null;
    }

    // 매니저 권한: 음식점 메뉴 등록
    @PostMapping("{restaurantId}/menus")
    public ResponseEntity<Void> createRestaurantMenu(
            @PathVariable RestaurantType restaurantType, @PathVariable Long restaurantId
    ) {
        return null;
    }

    // 매니저 권한: 음식점 메뉴 수정
    @PutMapping("{restaurantId}/menus/{menuId}")
    public ResponseEntity<Void> updateRestaurantMenu(
            @PathVariable RestaurantType restaurantType,
            @PathVariable Long restaurantId,
            @PathVariable Long menuId
    ) {
        return null;
    }

    // 매니저 권한: 음식점 메뉴 삭제
    @DeleteMapping("{restaurantId}/menus/{menuId}")
    public ResponseEntity<Void> deleteRestaurantMenu(
            @PathVariable RestaurantType restaurantType,
            @PathVariable Long restaurantId,
            @PathVariable Long menuId
    ) {
        return null;
    }
}
