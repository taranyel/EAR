package joinMe.db.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Setter
@Getter
@Entity
@NamedQueries({
        @NamedQuery(name = "Attendlist.findByTripAndJoiner", query = "SELECT a FROM Attendlist a WHERE a.trip = :trip AND a.joiner = :joiner"),
        @NamedQuery(name = "Attendlist.findByJoiner", query = "SELECT a FROM Attendlist a WHERE a.joiner = :joiner")
})
public class Attendlist extends AbstractEntity{

    public Attendlist() {
        init();
    }

    public Attendlist(User joiner, Trip trip) {
        this.joiner = joiner;
        this.trip = trip;
        init();
    }

    private void init() {
        messages = new ArrayList<>();
    }

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
