package joinMe.db.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Objects;

@Setter
@Getter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@NamedQueries({
        @NamedQuery(name = "Attendlist.findByTripAndJoiner", query = "SELECT a FROM Attendlist a WHERE a.trip = :trip AND a.joiner = :joiner"),
        @NamedQuery(name = "Attendlist.findByTrip", query = "SELECT a FROM Attendlist a WHERE a.trip = :trip")
})
public class Attendlist extends AbstractEntity{

    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "attendlist_id")
    private List<Message> messages;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User joiner;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Trip trip;

    public void addMessage(Message message) {
        Objects.requireNonNull(message);
        messages.add(message);
    }

    public User getAdmin() {
        return trip.getAuthor();
    }
}
