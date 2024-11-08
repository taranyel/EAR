package joinMe.db.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("FLAT")
public class Flat extends Address{
}
