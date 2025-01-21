package marryus.studressmake;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter @Setter
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

    public Sdm(){

    }

    public Sdm(Long id, String itemName, String shopName, String address, String phoneNumber, String description, int price, LocalDateTime createAt, LocalTime openTime, LocalTime closeTime, ShopCategory category) {
        this.id = id;
        this.itemName = itemName;
        this.shopName = shopName;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.description = description;
        this.price = price;
        this.createAt = createAt;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.category = category;
    }
}
