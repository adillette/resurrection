package marryus.studressmake.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import marryus.studressmake.CounselorStatus;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static marryus.studressmake.CounselorStatus.AVAILABLE;

@Entity
@Table(name = "MARRYUS_COUNSELOR")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Counselor {
    @Id
    @Column(name = "COUNSELOR_ID")
    private String counselorId; //cs1, cs2 ,cs3

    @Column(name = "COUNSELOR_NAME", nullable = false)
    private String counselorName;

    @Enumerated(value=EnumType.STRING)
    @Column(name = "STATUS", nullable = false) // nullable 추가
    private CounselorStatus status;

    @JsonIgnore
    @OneToMany(mappedBy = "counselor", cascade = CascadeType.ALL) // cascade 추가
    private List<ChatSession> sessions = new ArrayList<>();

    private int maxWorkload = 100; // 전체 최대 작업량
    private int currentWorkload = 0; // 현재 작업량
    private int dailyWorkload = 0; // 오늘의 작업량
    private int maxDailyWorkload = 20; // 하루 최대 작업량


    /**
     * 작업 가능여부 확인
     * function canAcceptMoreWork(){
     *     return status == AVAILABLE && currentWorkload<maxWorkload
     * }
     * public boolean canAcceptMoreWork(){
     *      return status == AVAILABLE && currentWorkload<maxWorkload
     * }
     *
     *
     *
     * 작업량 증가
     * function increaseWorkload(){
     *     curentWorkload++
     * }
     *
     */


/*
     * 작업 가능여부 확인
     * function canAcceptMoreWork(){
     *     return status == AVAILABLE && currentWorkload<maxWorkload
     * }
     *
     *
     *
     */

     public boolean canAcceptMoreWork(){

         return status == AVAILABLE && (currentWorkload<maxWorkload)
                 &&(dailyWorkload < maxDailyWorkload);
     }

    /**
     * 작업량 증가
     *     function increaseWorkload(){
     *        curentWorkload++
     *   }
     */
    public void increaseWorkload(){
        currentWorkload++;
        dailyWorkload++;
    }

    /**
     * 작업량 감소
     *  funcion decreaseWokload(){
     * if(currentWorkload>0){
     *   currentWorkload--
     *    }
     *
     */
    public void decreaseWorkload(){
        if(currentWorkload>0){
            currentWorkload--;
        }
    }
    // 일일 작업량 초기화 메소드 추가 (매일 자정에 실행될 수 있음)
    public void resetDailyWorkload() {
        dailyWorkload = 0;
    }


}


