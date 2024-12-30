package joinMe.db.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("HOUSE")
@NoArgsConstructor
public class House extends Address {
    public House(String city, String country, String number, String postIndex, String street) {
        super(city, country, number, postIndex, street);
    }
}
