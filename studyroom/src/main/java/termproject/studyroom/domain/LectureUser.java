package termproject.studyroom.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "LectureUser")
@IdClass(LectureUserId.class) // 복합 키 사용
public class LectureUser {

    @Id
    @ManyToOne
    @JoinColumn(name = "std_id", nullable = false)
    private User userId;

    @Id
    @ManyToOne
    @JoinColumn(name = "lecturelist_id", nullable = false)
    private LectureList lectureId;

}
