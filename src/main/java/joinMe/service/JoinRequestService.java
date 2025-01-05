package joinMe.service;

import jakarta.transaction.Transactional;
import joinMe.db.dao.JoinRequestDao;
import joinMe.db.entity.JoinRequest;
import joinMe.db.entity.RequestStatus;
import joinMe.db.entity.Trip;
import joinMe.db.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.security.access.AccessDeniedException;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class JoinRequestService {
    private final JoinRequestDao dao;

    @Autowired
    public JoinRequestService(JoinRequestDao dao) {
        this.dao = dao;
    }

    public void persist(JoinRequest joinRequest) {
        Objects.requireNonNull(joinRequest);
        dao.persist(joinRequest);
    }

    public JoinRequest findByID(Integer id) {
        return dao.find(id);
    }

    public List<JoinRequest> getJoinRequestsForApproval(User author) {
        Objects.requireNonNull(author);
        return dao.getJoinRequestsForApproval(author);
    }

    public List<JoinRequest> findByRequester(User requester) {
        Objects.requireNonNull(requester);
        return dao.findByRequester(requester);
    }

    public void remove(JoinRequest joinRequest) {
        Objects.requireNonNull(joinRequest);
        dao.remove(joinRequest);
    }

    public JoinRequest create(User requester, Trip trip) {
        Objects.requireNonNull(requester);
        Objects.requireNonNull(trip);

        User tripCreator = trip.getAuthor();
        if (requester == tripCreator) {
            throw new AccessDeniedException("User cannot create join request to the trip he/she is author of.");
        }

        JoinRequest existingRequest = dao.findByRequesterAndTrip(requester, trip);
        if (existingRequest != null && existingRequest.getStatus() != RequestStatus.REJECTED) {
            throw new AccessDeniedException("User cannot create more than one join request to one trip.");
        }

        return JoinRequest.builder()
                .requester(requester)
                .trip(trip)
                .build();
    }
}
