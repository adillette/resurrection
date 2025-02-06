package marryus.studressmake;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "COUNSELOR")
@Getter @Setter
@NoArgsConstructor
public class Counselor {
    @Id
    private String conselorId; //cs1, cs2 ,cs3

    private String counselorName;

    @Enumerated(EnumType.STRING)
    private CounselorStatus status;

    @OneToMany(mappedBy = "counselor")
    private List<ChatSession> sessions = new ArrayList<>();
}
