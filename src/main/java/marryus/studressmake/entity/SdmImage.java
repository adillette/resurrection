package marryus.studressmake.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
@Data
@Entity
@NoArgsConstructor
public class SdmImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long sdmId;  // Sdm의 ID만 참조
    private String fileName;
    private String originalFileName;

    @Builder
    public SdmImage(Long sdmId, String fileName, String originalFileName) {
        this.sdmId = sdmId;
        this.fileName = fileName;
        this.originalFileName = originalFileName;
    }

}
