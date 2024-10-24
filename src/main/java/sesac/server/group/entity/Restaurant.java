package sesac.server.group.entity;


import static org.springframework.util.StringUtils.hasText;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sesac.server.campus.entity.Campus;
import sesac.server.common.entity.HasCampus;
import sesac.server.group.dto.request.UpdateRestaurantRequest;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Restaurant implements HasCampus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(nullable = true)
    private String longitude;

    @Column(nullable = true)
    private String latitude;

    private String category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campus_id", nullable = false)
    private Campus campus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GroupType type;

    public void update(UpdateRestaurantRequest request) {
        if (hasText(request.name())) {
            this.name = request.name();
        }

        if (hasText(request.category())) {
            this.category = request.category();
        }

        if (hasText(request.address()) &&
                hasText(request.latitude()) &&
                hasText(request.longitude())) {
            this.address = request.address();
            this.latitude = request.latitude();
            this.longitude = request.longitude();
        }

    }
}