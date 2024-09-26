package sesac.server.group.entity;

import static org.springframework.util.StringUtils.hasText;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sesac.server.campus.entity.Campus;
import sesac.server.group.dto.request.UpdateMenuRequest;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Menu implements HasCampus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal price;

    public void update(UpdateMenuRequest request) {
        if (hasText(request.name())) {
            this.name = request.name();
        }
        if (request.price() != null) {
            this.price = request.price();
        }
    }

    @Override
    public Campus getCampus() {
        return this.restaurant.getCampus();
    }
}