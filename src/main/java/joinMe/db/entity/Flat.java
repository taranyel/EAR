package joinMe.db.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("FLAT")
@NoArgsConstructor
public class Flat extends Address {
    public Flat(String city, String country, String number, String postIndex, String street) {
        super(city, country, number, postIndex, street);
    }
}
