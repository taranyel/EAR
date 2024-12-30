package joinMe.db.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Setter
@Getter
@Entity
public abstract class Address extends AbstractEntity {

    public Address() {
        this.residents = new ArrayList<>();
    }

    public Address(String city, String country, String number, String postIndex, String street) {
        this.city = city;
        this.country = country;
        this.number = number;
        this.postIndex = postIndex;
        this.street = street;
        this.residents = new ArrayList<>();
    }

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

    @OneToMany
    @JoinColumn(name = "address_id")
    private List<User> residents;

    public void addResident(User resident) {
        Objects.requireNonNull(resident);
        residents.add(resident);
    }

    public void removeResident(User resident) {
        Objects.requireNonNull(resident);
        residents.remove(resident);
    }
}