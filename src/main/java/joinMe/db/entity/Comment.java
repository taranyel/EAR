package joinMe.db.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@NamedQueries({
        @NamedQuery(name = "Comment.findByTrip", query = "SELECT c FROM Comment c WHERE c.trip = :trip")
})
public class Comment extends AbstractEntity {
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
