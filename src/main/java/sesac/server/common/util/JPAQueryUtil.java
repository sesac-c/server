package sesac.server.common.util;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.PathBuilder;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Sort;

public class JPAQueryUtil {

    // Pageable의 Sort를 사용하여 QueryDSL의 order by 조건을 생성하는 메서드
    public static List<OrderSpecifier<?>> getOrderSpecifiers(EntityPathBase<?> qType, Sort sort) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
        PathBuilder entityPath = new PathBuilder<>(qType.getType(), qType.getMetadata());

        for (Sort.Order order : sort) {
            OrderSpecifier orderSpecifier = new OrderSpecifier(
                    order.isAscending() ? Order.ASC : Order.DESC,
                    entityPath.get(order.getProperty())
            );
            orderSpecifiers.add(orderSpecifier);
        }

        return orderSpecifiers;
    }
}
