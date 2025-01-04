package joinMe.db.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@NamedQueries({
        @NamedQuery(name = "Message.findByAuthor", query = "SELECT m FROM Message m WHERE m.author = :author"),
})
public class Message extends AbstractEntity {

    @Basic(optional = false)
    @Column(name = "text", nullable = false)
    private String text;

    @Basic(optional = false)
    @Column(name = "time", nullable = false)
    private LocalDateTime time;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Attendlist attendlist;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User author;
}
