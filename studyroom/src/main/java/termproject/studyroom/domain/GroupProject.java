package termproject.studyroom.domain;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@Table(name = "GroupProjects")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class GroupProject {

    @Id
    @Column(nullable = false, updatable = false)
    @SequenceGenerator(
            name = "primary_sequence_GroupProject",
            sequenceName = "primary_sequence_gp",
            allocationSize = 1,
            initialValue = 0
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "primary_sequence_GroupProject"
    )
    private Integer gpId;

    @Column(nullable = false)
    private Boolean groupValid = false; // 기본값 설정

    @Column(nullable = false, unique = true, length = 10)
    private String groupName;

    @Column(columnDefinition = "text")
    private String groupDetail;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_user_id_id", nullable = false)
    private User createdUserId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lecture_id_id", nullable = false)
    private LectureList lectureId;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @LastModifiedDate
    @Column(nullable = false)
    private OffsetDateTime lastUpdated;

    @OneToMany(
            mappedBy = "groupProject", // GroupUser에서 groupProject와의 매핑 필드
            cascade = CascadeType.ALL, // 부모가 삭제되면 자식도 삭제
            orphanRemoval = true // 고아 엔티티(연결이 끊긴 자식 엔티티) 자동 삭제
    )
    private List<GroupUser> groupUsers = new ArrayList<>();


}
