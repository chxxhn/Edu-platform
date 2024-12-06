package termproject.studyroom.domain;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class GroupUserId implements Serializable {

        private Integer userId;
    private Integer groupId;

    public GroupUserId() {}

    public GroupUserId(Integer userId, Integer groupId) {
        this.userId = userId;
        this.groupId = groupId;
    }

    // equals and hashCode methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupUserId that = (GroupUserId) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(groupId, that.groupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, groupId);
    }
}
