package io.appform.statesman.server.dao.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Entity
@Table(name = "message", uniqueConstraints = {
        @UniqueConstraint(columnNames = "transition_id")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Id
    @Column(name = "language")
    private String language;

    @Id
    @Column(name = "state")
    private String state;

    @Column(name = "message_text")
    private String messageText;

    @Builder
    public Message(String language,
                   String state,
                   String messageText) {
        this.language = language;
        this.state = state;
        this.messageText = messageText;
    }

}
