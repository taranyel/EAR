package joinMe.db.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@NamedQueries({
        @NamedQuery(name = "Address.findAll", query = "SELECT a FROM Address a"),
        @NamedQuery(name = "Address.findByAll", query = "SELECT a FROM Address a WHERE a.country = :country AND a.city = :city AND a.number = :number AND a.postIndex = :postIndex AND a.street = :street")
})
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

    @OneToMany(mappedBy = "address", fetch = FetchType.EAGER)
    @Builder.Default
    @JsonIgnore
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