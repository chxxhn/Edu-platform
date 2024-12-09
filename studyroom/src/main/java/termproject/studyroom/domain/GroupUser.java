package termproject.studyroom.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import termproject.studyroom.model.Team;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "GroupUser")
public class GroupUser {

    @EmbeddedId
    private GroupUserId groupUserId; // 복합 키 클래스

    @ManyToOne
    @MapsId("userId") // 복합 키의 userId 필드를 매핑
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @MapsId("lectureId") // 복합 키의 lectureId 필드를 매핑
    @JoinColumn(name = "lecture_id", nullable = false)
    private LectureList lectureList;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private GroupProject groupProject;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Team team;
}
