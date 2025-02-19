package marryus.studressmake.entity;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Data
@Slf4j
public class SdmPageResponseDTO<E> {

    private final List<E> dtoList;
    private final List<Integer> pageNumList;
    private final SdmPageRequestDTO pageRequestDTO;
    private final boolean prev, next;
    private final int totalCount, prevPage, nextPage, totalPage, current;



    @Builder
    private SdmPageResponseDTO(List<E> dtoList, SdmPageRequestDTO pageRequestDTO,
                               long totalCount){
        this.dtoList = dtoList;
        this.pageRequestDTO = pageRequestDTO;
        this.totalCount = (int)totalCount;

        //총 페이지수 계산
        int last = (int) Math.ceil((double) totalCount / pageRequestDTO.getSize());
        last = Math.max(last, 1);

        //끝 페이지 계산
        int end = (int) Math.ceil(pageRequestDTO.getPage() / 10.0) * 10;
        end = Math.min(end, last); // 끝 페이지는 총 페이지 수보다 클 수 없음

        // 시작 페이지 계산
        int start = Math.max(end - 9, 1); // 최소 페이지는 1
        System.out.println("Start Page: " + start + ", End Page: " + end + ", Total Pages: " + last);
        // 이전 페이지 존재 여부
        this.prev = start > 1;

        // 다음 페이지 존재 여부
        this.next = end < last;

        // 페이지 번호 리스트 생성
        this.pageNumList = IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());
        System.out.println("pagenumberlist: " + pageNumList); // 디버깅 로그

        // 이전 페이지와 다음 페이지 계산
        this.prevPage = this.prev ? start - 1 : 0;
        this.nextPage = this.next ? end + 1 : 0;

        this.totalPage = last;
        this.current = pageRequestDTO.getPage();

    }
    public static <E> SdmPageResponseDTO<E> of(List<E> dtoList, SdmPageRequestDTO pageRequestDTO, long totalCount) {

        System.out.println("totalcount"+ totalCount);
        return new SdmPageResponseDTO<>((List<E>) dtoList, pageRequestDTO, totalCount);
    }

}
