package termproject.studyroom.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import termproject.studyroom.model.Team;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
    @MapsId("groupId") // 복합 키의 groupId 필드를 매핑
    @JoinColumn(name = "group_id", nullable = false)
    private GroupProject groupProject;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Team team;
}
