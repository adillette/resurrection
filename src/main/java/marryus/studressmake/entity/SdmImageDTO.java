package marryus.studressmake.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SdmImageDTO {
    private Long id;
    private Long sdmId;
    private String fileName;
    private String originalFileName;
}