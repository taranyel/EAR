package joinMe.db.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@NamedQueries({
        @NamedQuery(name = "Wishlist.findTripsByOwner", query = "SELECT w FROM Wishlist w WHERE w.owner = :owner")
})
public class Wishlist extends AbstractEntity {

    @ManyToOne
    @JoinColumn(nullable = false)
    private User owner;

    @ManyToOne
    @JoinColumn
    private Trip trip;

    @Override
    public String toString() {
        return "Wishlist{" +
                "\n owner=" + owner +
                ",\n trip=" + trip +
                "\n}";
    }
}
