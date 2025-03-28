package marryus.studressmake.entity;

import lombok.*;
import marryus.studressmake.ShopCategory;

import java.util.Objects;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Sdm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String itemName;
    private String shopName;
    private String address;
    private String phoneNumber;

    private String description;
    private  int price;
    private LocalDateTime createAt;
    private LocalTime openTime;
    private LocalTime closeTime;

    @Enumerated(EnumType.STRING)
    private ShopCategory category;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sdm sdm = (Sdm) o;
        return Objects.equals(id, sdm.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Sdm{" +
                "id=" + id +
                ", itemName='" + itemName + '\'' +
                ", shopName='" + shopName + '\'' +
                ", address='" + address + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", createAt=" + createAt +
                ", openTime=" + openTime +
                ", closeTime=" + closeTime +
                ", category=" + category +
                '}';
    }

}
