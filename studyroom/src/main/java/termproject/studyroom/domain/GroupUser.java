package termproject.studyroom.domain;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import termproject.studyroom.model.Grade;
import termproject.studyroom.model.Team;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "GroupUser")
public class GroupUser {

    @EmbeddedId
    private GroupUserId GroupUserId;

    @ManyToOne
    @MapsId("userId") // LectureUserId의 userId를 매핑
    @JoinColumn(name = "std_id", nullable = false)
    private User user;

    @ManyToOne
    @MapsId("groupId") // LectureUserId의 lectureId를 매핑
    @JoinColumn(name = "group_Id", nullable = false)
    private GroupProject group;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Team team;

    @ManyToOne
    @MapsId("lecture_Id")
    @JoinColumn(name= "lecture_Id", nullable = false)
    private LectureList lectureList;
}
