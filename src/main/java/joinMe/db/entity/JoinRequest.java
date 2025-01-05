package joinMe.db.entity;

import jakarta.persistence.*;
import joinMe.util.Constants;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@NamedQueries({
        @NamedQuery(name = "JoinRequest.findByRequester", query = "SELECT j FROM JoinRequest j WHERE j.requester = :requester"),
        @NamedQuery(name = "JoinRequest.findByTrip", query = "SELECT j FROM JoinRequest j WHERE j.trip = :trip"),
        @NamedQuery(name = "JoinRequest.findByRequesterAndTrip", query = "SELECT j FROM JoinRequest j WHERE j.requester = :requester AND j.trip = :trip"),
        @NamedQuery(name = "JoinRequest.getJoinRequestsForApproval", query = "SELECT j FROM JoinRequest j LEFT JOIN Trip t ON j.trip = t WHERE t.author = :author AND j.status = 'IN_PROGRESS'")
})
public class JoinRequest extends AbstractEntity {

    @ManyToOne
    @JoinColumn(nullable = false)
    User requester;

    @ManyToOne
    @JoinColumn(nullable = false)
    Trip trip;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    private RequestStatus status = Constants.DEFAULT_REQUEST_STATUS;

    @Override
    public String toString() {
        return "JoinRequest{" +
                "\n requester=" + requester +
                ",\n trip=" + trip +
                ",\n status=" + status +
                "\n}";
    }
}
