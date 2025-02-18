package marryus.studressmake.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import marryus.studressmake.CounselorStatus;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false) // nullable 추가
    private CounselorStatus status;

    @OneToMany(mappedBy = "counselor", cascade = CascadeType.ALL) // cascade 추가
    private List<ChatSession> sessions = new ArrayList<>();


}
