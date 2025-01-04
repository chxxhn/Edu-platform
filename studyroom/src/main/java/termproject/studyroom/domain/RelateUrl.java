package termproject.studyroom.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
public class RelateUrl {
    @Column(nullable = false)
    private String url;

    public RelateUrl(String url) {
        this.url = url;
    }
}