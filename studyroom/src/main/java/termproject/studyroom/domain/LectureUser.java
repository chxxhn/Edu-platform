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
    @JoinColumn(name = "Users", referencedColumnName = "stdId") // User 테이블의 PK 참조
    private User userId;

    @Id
    @ManyToOne
    @JoinColumn(name = "lectureid", referencedColumnName = "lectureid")
    private LectureList lectureId;

}
