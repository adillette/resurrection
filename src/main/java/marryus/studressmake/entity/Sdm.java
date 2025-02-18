package marryus.studressmake.entity;

import lombok.*;
import marryus.studressmake.ShopCategory;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
    private ShopCategory category;

}
