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
        @NamedQuery(name = "Wishlist.findTripsByOwner", query = "SELECT w.trip FROM Wishlist w WHERE w.owner = :owner")
})
public class Wishlist extends AbstractEntity {

    public Wishlist(User owner, Trip trip) {
        this.owner = owner;
        this.trip = trip;
    }

    @ManyToOne
    @JoinColumn(nullable = false)
    private User owner;

    @ManyToOne
    @JoinColumn
    private Trip trip;
}
