package termproject.studyroom.domain;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class GroupUserId implements Serializable {

    private Integer userId;
    private Integer lectureId;
    private Integer groupId;

    public GroupUserId() {}

    public GroupUserId(Integer userId, Integer lectureId, Integer groupId) {
        this.userId = userId;
        this.lectureId = lectureId;
        this.groupId = groupId;
    }

    // Getter and Setter
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getLectureId() {
        return lectureId;
    }

    public void setLectureId(Integer lectureId) {
        this.lectureId = lectureId;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    // equals() and hashCode() 구현
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupUserId that = (GroupUserId) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(lectureId, that.lectureId) &&
                Objects.equals(groupId, that.groupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, lectureId, groupId);
    }
}
