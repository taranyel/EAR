package joinMe.db.entity;

import jakarta.persistence.*;
import joinMe.util.Constants;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Setter
@Getter
@Entity
@NamedQueries({
        @NamedQuery(name = "Trip.findByStatus", query = "SELECT t FROM Trip t WHERE t.status = :status"),
        @NamedQuery(name = "Trip.findByCountry", query = "SELECT t FROM Trip t WHERE t.country = :country"),
        @NamedQuery(name = "Trip.findByStartDate", query = "SELECT t FROM Trip t WHERE t.startDate = :startDate"),
        @NamedQuery(name = "Trip.findByEndDate", query = "SELECT t FROM Trip t WHERE t.endDate = :endDate"),
        @NamedQuery(name = "Trip.findByCapacity", query = "SELECT t FROM Trip t WHERE t.capacity = :capacity"),
        @NamedQuery(name = "Trip.findByAuthor", query = "SELECT t FROM Trip t WHERE t.author = :author"),
        @NamedQuery(name = "Trip.findInWishlistByOwner", query = "SELECT w.trip FROM Wishlist w WHERE w.owner = :owner")
})
public class Trip extends AbstractEntity {

    public Trip() {
        comments = new ArrayList<>();
        attendlists = new ArrayList<>();
        status = Constants.DEFAULT_TRIP_STATUS;
        created = LocalDateTime.now();
    }

    public Trip(String title, User author, Integer capacity, String country, String description, Date endDate, String imagePath, Date startDate) {
        this.title = title;
        this.author = author;
        this.capacity = capacity;
        this.country = country;
        this.description = description;
        this.endDate = endDate;
        this.imagePath = imagePath;
        this.startDate = startDate;
        created = LocalDateTime.now();
        comments = new ArrayList<>();
        attendlists = new ArrayList<>();
        status = Constants.DEFAULT_TRIP_STATUS;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TripStatus status;

    @Basic(optional = false)
    @Column(name = "title", nullable = false)
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
    @Column(name = "start_date", nullable = false)
    private Date startDate;

    @Basic(optional = false)
    @Column(name = "end_date", nullable = false)
    private Date endDate;

    @Basic(optional = false)
    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User author;

    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "trip_id")
    private List<Comment> comments;

    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "trip_id")
    private List<Attendlist> attendlists;

    public void addComment(Comment comment) {
        Objects.requireNonNull(comment);
        comments.add(comment);
    }

    public void removeComment(Comment comment) {
        Objects.requireNonNull(comment);
        comments.remove(comment);
    }

    @Override
    public String toString() {
        return "Trip{" +
                "attendlists=" + attendlists +
                ", status=" + status +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", country='" + country + '\'' +
                ", capacity=" + capacity +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", created=" + created +
                ", author=" + author +
                ", comments=" + comments +
                '}';
    }
}
