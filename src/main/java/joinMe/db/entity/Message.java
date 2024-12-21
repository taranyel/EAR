package joinMe.db.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@NamedQueries({
        @NamedQuery(name = "Message.findByAuthor", query = "SELECT m FROM Message m WHERE m.author = :author"),
        @NamedQuery(name = "Message.findByAttendList", query = "SELECT m FROM Message m WHERE m.attendlist = :attendlist")
})
public class Message extends AbstractEntity {
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
