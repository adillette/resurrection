package marryus.studressmake;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.DoubleStream;

@Entity
@Table(name = "marryus_counselor")
@Getter @Setter
@NoArgsConstructor
public class Counselor {
    @Id
    @Column(name = "COUNSELOR_ID")
    private String conselorId; //cs1, cs2 ,cs3

    @Column(name = "COUNSELOR_NAME", nullable = false)
    private String counselorName;

    @Enumerated(EnumType.STRING)
    private CounselorStatus status;

    @OneToMany(mappedBy = "counselor")
    private List<ChatSession> sessions = new ArrayList<>();


}
