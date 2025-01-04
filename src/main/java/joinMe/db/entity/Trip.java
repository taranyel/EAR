package joinMe.db.entity;

import jakarta.persistence.*;
import joinMe.util.Constants;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@NamedQueries({
        @NamedQuery(name = "Trip.findByStatus", query = "SELECT t FROM Trip t WHERE t.status = :status"),
        @NamedQuery(name = "Trip.findByAuthor", query = "SELECT t FROM Trip t WHERE t.author = :author")
})
public class Trip extends AbstractEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    private TripStatus status = Constants.DEFAULT_TRIP_STATUS;

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
    private LocalDate startDate;

    @Basic(optional = false)
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Basic(optional = false)
    @Column(name = "created", nullable = false)
    @Builder.Default
    private LocalDateTime created = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(nullable = false)
    private User author;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("time DESC")
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "trip", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Attendlist> attendlists = new ArrayList<>();

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<JoinRequest> joinRequests = new ArrayList<>();

    public void addComment(Comment comment) {
        Objects.requireNonNull(comment);
        comments.add(comment);
    }

    public void removeComment(Comment comment) {
        Objects.requireNonNull(comment);
        comments.remove(comment);
    }

    public void addAttendlist(Attendlist attendlist) {
        Objects.requireNonNull(attendlist);
        attendlists.add(attendlist);
    }

    public void removeAttendlist(Attendlist attendlist) {
        Objects.requireNonNull(attendlist);
        attendlists.remove(attendlist);
    }
}
