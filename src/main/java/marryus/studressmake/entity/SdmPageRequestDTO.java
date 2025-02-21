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

    private String category;    // 카테고리 필터링
    private String sort;        // 정렬 기준
    private String direction;   // 정렬 방향
    private String itemName;    // 검색어

    // Pageable 객체를 반환하는 메소드 추가
    public Pageable getPageable() {
        int validPage = Math.max(page - 1, 0); // 페이지가 1보다 작으면 0으로 설정
        return PageRequest.of(validPage, size, Sort.by("id").descending()); // 기본 정렬 기준 추가
    }

    private Sort getSort() {
        if (sort == null) return Sort.by("id").descending();

        Sort.Direction dir = "ASC".equals(direction) ?
                Sort.Direction.ASC : Sort.Direction.DESC;

        return Sort.by(dir, sort);
    }


}
