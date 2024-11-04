package joinMe.db.entity;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "EAR_USER")
@NamedQueries({
        @NamedQuery(name = "User.findByUsername", query = "SELECT u FROM User u WHERE u.username = :username")
})
public class User extends AbstractEntity{
    @Basic(optional = false)
    @Column(name="first_name", nullable = false)
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

    @Basic(optional = false)
    @Column(name = "birthdate", nullable = false)
    private Date birthdate;

    @Basic(optional = false)
    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Basic(optional = false)
    @Column(name = "image_path", nullable = false)
    private String imagePath;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Address address;

    @OneToMany(mappedBy = "accused")
    @JoinColumn(name = "id_user")
    private List<Complaint> complaints;

    @OneToMany
    @JoinColumn(name = "id_user")
    private List<UserInChat> userInChats;

    @OneToMany(mappedBy = "author")
    @OrderBy("created")
    @JoinColumn(name = "id_user")
    private List<Post> posts;

    @OneToOne
    private Wishlist wishlist;

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Wishlist getWishlist() {
        return wishlist;
    }

    public void setWishlist(Wishlist wishlist) {
        this.wishlist = wishlist;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<UserInChat> getUserInChats() {
        return userInChats;
    }

    public void setUserInChats(List<UserInChat> userInChats) {
        this.userInChats = userInChats;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Complaint> getComplaints() {
        return complaints;
    }

    public void setComplaints(List<Complaint> complaints) {
        this.complaints = complaints;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public String toString() {
        return "User{" +
                "address=" + address +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", birthdate=" + birthdate +
                ", rating=" + rating +
                ", imagePath='" + imagePath + '\'' +
                ", complaints=" + complaints +
                ", userInChats=" + userInChats +
                ", posts=" + posts +
                ", wishlist=" + wishlist +
                '}';
    }
}
