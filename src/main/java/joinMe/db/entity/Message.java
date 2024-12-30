package joinMe.db.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@NoArgsConstructor
@NamedQueries({
        @NamedQuery(name = "Message.findByAuthor", query = "SELECT m FROM Message m WHERE m.author = :author"),
        @NamedQuery(name = "Message.findByAttendList", query = "SELECT m FROM Message m WHERE m.attendlist = :attendlist")
})
public class Message extends AbstractEntity {

    public Message(Attendlist attendlist, User author, String text) {
        this.attendlist = attendlist;
        this.author = author;
        this.text = text;
    }

    @Basic(optional = false)
    @Column(name="text", nullable = false)
    private String text;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Attendlist attendlist;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User author;
}
