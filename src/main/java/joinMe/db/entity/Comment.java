package joinMe.db.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@NoArgsConstructor
@NamedQueries({
        @NamedQuery(name = "Comment.findByTrip", query = "SELECT c FROM Comment c WHERE c.trip = :trip")
})
public class Comment extends AbstractEntity {

    public Comment(User author, String text, Trip trip) {
        this.author = author;
        this.text = text;
        this.trip = trip;
    }

    @Basic(optional = false)
    @Column(name="text", nullable = false)
    private String text;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Trip trip;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User author;
}
