package joinMe.db.entity;

import jakarta.persistence.*;
import joinMe.util.Constants;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@NamedQueries({
        @NamedQuery(name = "JoinRequest.findByRequester", query = "SELECT j FROM JoinRequest j WHERE j.requester = :requester"),
        @NamedQuery(name = "JoinRequest.findByRequesterAndTrip", query = "SELECT j FROM JoinRequest j WHERE j.requester = :requester AND j.trip = :trip"),
        @NamedQuery(name = "JoinRequest.getJoinRequestsForApproval", query = "SELECT j FROM JoinRequest j LEFT JOIN Trip t ON j.trip = t WHERE t.author = :author")
})
public class JoinRequest extends AbstractEntity {
    public JoinRequest() {
        status = Constants.DEFAULT_REQUEST_STATUS;
    }

    @ManyToOne
    @JoinColumn(nullable = false)
    User requester;

    @ManyToOne
    @JoinColumn(nullable = false)
    Trip trip;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RequestStatus status;
}
