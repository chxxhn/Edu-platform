package termproject.studyroom.domain;

import jakarta.persistence.JoinColumn;

public class   LectureUserId {
    @JoinColumn(name = "std_id")
    private User userId;

    @JoinColumn(name="lecturelist_id")
    private LectureList lectureListId;
}
