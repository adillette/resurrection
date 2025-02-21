package marryus.studressmake.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SdmSearchDTO {
    private String category;
    private String sort;
    private String direction;
    private String itemName;


}
