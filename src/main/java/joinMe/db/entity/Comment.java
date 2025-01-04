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

    @Basic(optional = false)
    @Column(name = "time", nullable = false)
    private LocalDateTime time;
}
