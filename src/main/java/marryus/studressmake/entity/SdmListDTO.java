package marryus.studressmake.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SdmListDTO {

    private Long id;
    private String itemName;
    private String shopName;
    private int price;
    private String category;
    private List<String> uploadFileNames;

}
