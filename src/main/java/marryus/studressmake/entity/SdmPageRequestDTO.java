package marryus.studressmake.entity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class SdmPageRequestDTO {
    @Builder.Default
    private int page=1;

    @Builder.Default
    private int size=12;

    // Pageable 객체를 반환하는 메소드 추가
    public Pageable getPageable() {
        int validPage = Math.max(page - 1, 0); // 페이지가 1보다 작으면 0으로 설정
        return PageRequest.of(validPage, size, Sort.by("id").descending()); // 기본 정렬 기준 추가
    }
}
