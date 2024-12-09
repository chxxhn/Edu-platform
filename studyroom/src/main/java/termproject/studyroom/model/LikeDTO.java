
package termproject.studyroom.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import termproject.studyroom.domain.User;

import java.time.OffsetDateTime;

@Getter
@Setter
public class LikeDTO {

    private Integer id;          // Like의 고유 ID

    @NotNull
    private User author;      // 좋아요를 누른 사용자 ID

    @NotNull
    private Integer postId;      // 게시글 ID

    @NotNull
    private BoardType boardType;

//    private OffsetDateTime dateCreated;  // 생성 날짜
//
//    private OffsetDateTime lastUpdated;  // 수정 날짜

}
