package termproject.studyroom.domain;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.lang.annotation.Retention;

@Entity
@NoArgsConstructor
@Getter
@ToString()
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Embedded
    private NotificationContent content;

    @Embedded
    private RelateUrl url;

    private String toName;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User receiver;

    @Builder
    public Notification(User receiver, NotificationType notificationType, String content,
                        String url, String toName) {
        this.receiver = receiver;
        this.notificationType = notificationType;
        this.toName = toName;
        this.content = new NotificationContent(content);
        this.url = new RelateUrl(url);
    }

    public String getContent() {
        return content.getContent();
    }

    public String getUrl() {
        return url.getUrl();
    }


}
