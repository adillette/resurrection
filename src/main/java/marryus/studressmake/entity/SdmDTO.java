package marryus.studressmake.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import marryus.studressmake.ShopCategory;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SdmDTO {
    private Long id;
    private String itemName;
    private String shopName;
    private String address;
    private String phoneNumber;
    private String description;
    private int price;
    private LocalDateTime createAt;
    private LocalTime openTime;
    private LocalTime closeTime;
    private ShopCategory category;
}
