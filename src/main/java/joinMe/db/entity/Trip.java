package joinMe.db.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@NamedQueries({
        @NamedQuery(name = "Trip.findByCountry", query = "SELECT p FROM Trip p WHERE p.country = :country")
})
public class Trip extends AbstractEntity{
    @Basic(optional = false)
    @Column(name="title", nullable = false)
    private String title;

    @Basic(optional = false)
    @Column(name = "description", nullable = false)
    private String description;

    @Basic(optional = false)
    @Column(name = "image_path", nullable = false)
    private String imagePath;

    @Basic(optional = false)
    @Column(name = "country", nullable = false)
    private String country;

    @Basic(optional = false)
    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Basic(optional = false)
    @Column(name = "startDate", nullable = false)
    private LocalDate startDate;

    @Basic(optional = false)
    @Column(name = "tillDate", nullable = false)
    private Date tillDate;

    @Basic(optional = false)
    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User author;

    @OneToMany
    @JoinColumn(name = "trip_id")
    List<Attendlist> attendlists;

    public List<Attendlist> getAttendlists() {
        return attendlists;
    }

    public void setAttendlists(List<Attendlist> attendlists) {
        this.attendlists = attendlists;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public Date getTillDate() {
        return tillDate;
    }

    public void setTillDate(Date till) {
        this.tillDate = till;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    //    @OneToMany
//    @JoinColumn(name = "trip_id")
//    private List<Comment> comments;
//
//    @OneToMany
//    @JoinColumn(name = "trip_id")
//    private List<Wishlist> tripInWishlist;
//
//    public AttendList getAttendList() {
//        return attendList;
//    }
//
//    public void setAttendList(AttendList attendList) {
//        this.attendList = attendList;
//    }
//
//    public List<Wishlist> getTripInWishlist() {
//        return tripInWishlist;
//    }
//
//    public void setTripInWishlist(List<Wishlist> tripInWishlist) {
//        this.tripInWishlist = tripInWishlist;
//    }
//
//
//    public User getAuthor() {
//        return author;
//    }
//
//    public void setAuthor(User author) {
//        this.author = author;
//    }
//
//    public String getTitle() {
//        return title;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }
//
//    public Date getTill() {
//        return till;
//    }
//
//    public void setTill(Date till) {
//        this.till = till;
//    }
//
//    public String getImagePath() {
//        return imagePath;
//    }
//
//    public void setImagePath(String imagePath) {
//        this.imagePath = imagePath;
//    }
//
//    public Date getFrom() {
//        return from;
//    }
//
//    public void setFrom(Date from) {
//        this.from = from;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }
//
//    public LocalDateTime getCreated() {
//        return created;
//    }
//
//    public void setCreated(LocalDateTime created) {
//        this.created = created;
//    }
//
//    public String getCountry() {
//        return country;
//    }
//
//    public void setCountry(String country) {
//        this.country = country;
//    }
//
//    public List<Comment> getComments() {
//        return comments;
//    }
//
//    public void setComments(List<Comment> comments) {
//        this.comments = comments;
//    }
//
//    public Integer getCapacity() {
//        return capacity;
//    }
//
//    public void setCapacity(Integer capacity) {
//        this.capacity = capacity;
//    }
}
