package termproject.studyroom.domain;

import jakarta.persistence.JoinColumn;

public class LectureUserId {
    @JoinColumn(name = "std_id")
    private Integer userId;

    @JoinColumn(name="lecturelist_id")
    private Integer lectureId;
}
