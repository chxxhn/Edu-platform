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
public class LectureUser {

    @EmbeddedId
    private LectureUserId id;

    @ManyToOne
    @MapsId("userId") // LectureUserId의 userId를 매핑
    @JoinColumn(name = "std_id", nullable = false)
    private User user;

    @ManyToOne
    @MapsId("lectureId") // LectureUserId의 lectureId를 매핑
    @JoinColumn(name = "lecturelist_id", nullable = false)
    private LectureList lecture;
}
