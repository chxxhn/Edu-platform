package termproject.studyroom.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "group_approve")
public class GroupApprove {

    @Id
    @Column(nullable = false, unique = true)
    @SequenceGenerator(
            name = "primary_sequence_ApId",
            sequenceName = "primary_sequence_ApId",
            allocationSize = 1,
            initialValue = 0
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "primary_sequence_ApId"
    )
    private Integer approveId;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private GroupProject groupProject;

    @ManyToOne
    @JoinColumn(name = "lecture_id", nullable = false)
    private LectureList lectureList;
}
