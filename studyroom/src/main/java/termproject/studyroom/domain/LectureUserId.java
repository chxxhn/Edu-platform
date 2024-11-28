package termproject.studyroom.domain;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class LectureUserId implements Serializable {

    private Integer userId;
    private Integer lectureId;

    public LectureUserId() {}

    public LectureUserId(Integer userId, Integer lectureId) {
        this.userId = userId;
        this.lectureId = lectureId;
    }

    // equals and hashCode methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LectureUserId that = (LectureUserId) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(lectureId, that.lectureId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, lectureId);
    }
}
