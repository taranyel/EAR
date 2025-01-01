package joinMe.db.entity;

import jakarta.persistence.*;
import joinMe.util.Constants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@NamedQueries({
        @NamedQuery(name = "JoinRequest.findByRequester", query = "SELECT j FROM JoinRequest j WHERE j.requester = :requester"),
        @NamedQuery(name = "JoinRequest.findByRequesterAndTrip", query = "SELECT j FROM JoinRequest j WHERE j.requester = :requester AND j.trip = :trip"),
        @NamedQuery(name = "JoinRequest.getJoinRequestsForApproval", query = "SELECT j FROM JoinRequest j LEFT JOIN Trip t ON j.trip = t WHERE t.author = :author")
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
    private RequestStatus status;
}
