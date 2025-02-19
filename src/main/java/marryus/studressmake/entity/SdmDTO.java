package marryus.studressmake.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import marryus.studressmake.ShopCategory;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

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

    // 파일 관련 필드
    private List<MultipartFile> files;  // 업로드할 파일들
    private List<String> uploadFileNames;  // 저장된 파일 이름들

    // DTO -> Entity 변환
    public Sdm toEntity() {
        return Sdm.builder()
                .id(id)
                .itemName(itemName)
                .shopName(shopName)
                .address(address)
                .phoneNumber(phoneNumber)
                .description(description)
                .price(price)
                .createAt(createAt)
                .openTime(openTime)
                .closeTime(closeTime)
                .category(category)
                .build();
    }

    // Entity -> DTO 변환
    public static SdmDTO fromEntity(Sdm entity) {
        return SdmDTO.builder()
                .id(entity.getId())
                .itemName(entity.getItemName())
                .shopName(entity.getShopName())
                .address(entity.getAddress())
                .phoneNumber(entity.getPhoneNumber())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .createAt(entity.getCreateAt())
                .openTime(entity.getOpenTime())
                .closeTime(entity.getCloseTime())
                .category(entity.getCategory())
                .build();
    }
}
