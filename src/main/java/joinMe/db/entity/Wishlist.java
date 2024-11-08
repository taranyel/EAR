package joinMe.db.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@NamedQueries({
        @NamedQuery(name = "Wishlist.findTripsByOwner", query = "SELECT w.trip FROM Wishlist w WHERE w.owner = :owner")
})
public class Wishlist extends AbstractEntity {
    @ManyToOne
    @JoinColumn(nullable = false)
    private User owner;

    @ManyToOne
    @JoinColumn
    private Trip trip;
}
