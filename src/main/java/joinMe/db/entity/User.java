package joinMe.db.entity;

import jakarta.persistence.*;
import joinMe.util.Constants;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Setter
@Getter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "EAR_USER")
@NamedQueries({
        @NamedQuery(name = "User.findByUsername", query = "SELECT u FROM User u WHERE u.username = :username"),
        @NamedQuery(name = "User.findByEmail", query = "SELECT u FROM User u WHERE u.email = :email"),
        @NamedQuery(name = "User.getAllJoinersOfAttendlistByTrip", query = "SELECT a.joiner FROM Attendlist a WHERE a.trip = :trip"),
        @NamedQuery(name = "User.getAllJoinersOfAttendlistById", query = "SELECT a.joiner FROM Attendlist a WHERE a.id = :id")
})
public class User extends AbstractEntity {

    @Basic(optional = false)
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Basic(optional = false)
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Basic(optional = false)
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Basic(optional = false)
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Basic(optional = false)
    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AccountStatus status;

    @Basic(optional = false)
    @Column(name = "birthdate", nullable = false)
    private LocalDate birthdate;

    @Basic(optional = false)
    @Column(name = "rating", nullable = false)
    private Integer rating = 0;

    @Column(name = "image_path")
    private String imagePath;

    @ManyToOne(optional = false, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(nullable = false)
    private Address address;

    @OneToMany(mappedBy = "accused", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Complaint> complaints;

    @OneToMany(mappedBy = "joiner")
    private List<Attendlist> attendlists;

    @OneToMany(mappedBy = "author", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("created")
    private List<Trip> trips;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Wishlist> wishlists;

    @OneToMany(mappedBy = "requester", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<JoinRequest> joinRequests;

    public void encodePassword(PasswordEncoder encoder) {
        this.password = encoder.encode(password);
    }

    public void erasePassword() {
        this.password = null;
    }

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }

    public void addTrip(Trip trip) {
        Objects.requireNonNull(trip);
        trips.add(trip);
    }

    public void removeTrip(Trip trip) {
        Objects.requireNonNull(trip);
        trips.remove(trip);
    }

    public void addComplaint(Complaint complaint) {
        Objects.requireNonNull(complaint);
        complaints.add(complaint);
    }

    public void removeComplaint(Complaint complaint) {
        Objects.requireNonNull(complaint);
        complaints.remove(complaint);
    }

    public void addAttendlist(Attendlist attendlist) {
        Objects.requireNonNull(attendlist);
        attendlists.add(attendlist);
    }

    public void removeAttendlist(Attendlist attendlist) {
        Objects.requireNonNull(attendlist);
        attendlists.remove(attendlist);
    }

    public void addWishlist(Wishlist wishlist) {
        Objects.requireNonNull(wishlist);
        wishlists.add(wishlist);
    }

    public void removeWishlist(Wishlist wishlist) {
        Objects.requireNonNull(wishlist);
        wishlists.remove(wishlist);
    }

    public void addJoinRequest(JoinRequest joinRequest) {
        Objects.requireNonNull(joinRequest);
        joinRequests.add(joinRequest);
    }

    public void removeJoinRequest(JoinRequest joinRequest) {
        Objects.requireNonNull(joinRequest);
        joinRequests.remove(joinRequest);
    }
}
