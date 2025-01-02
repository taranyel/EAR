package joinMe.db.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Setter
@Getter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class Address extends AbstractEntity {

    @Basic(optional = false)
    @Column(name = "city", nullable = false)
    private String city;

    @Basic(optional = false)
    @Column(name = "street", nullable = false)
    private String street;

    @Basic(optional = false)
    @Column(name = "number", nullable = false)
    private String number;

    @Basic(optional = false)
    @Column(name = "post_index", nullable = false)
    private String postIndex;

    @Basic(optional = false)
    @Column(name = "country", nullable = false)
    private String country;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "address_id")
    @Builder.Default
    private List<User> residents = new ArrayList<>();

    public void addResident(User resident) {
        Objects.requireNonNull(resident);
        residents.add(resident);
    }

    public void removeResident(User resident) {
        Objects.requireNonNull(resident);
        residents.remove(resident);
    }
}