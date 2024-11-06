package joinMe.db.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Attendlist extends AbstractEntity{
    @OneToMany
    @JoinColumn(name = "attendlist_id")
    private List<Message> messages;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User joiner;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Trip trip;

    public User getJoiner() {
        return joiner;
    }

    public void setJoiner(User user) {
        this.joiner = user;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
