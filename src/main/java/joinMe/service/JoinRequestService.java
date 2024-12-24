package joinMe.service;

import jakarta.transaction.Transactional;
import joinMe.db.dao.JoinRequestDao;
import joinMe.db.entity.JoinRequest;
import joinMe.db.entity.RequestStatus;
import joinMe.db.entity.Trip;
import joinMe.db.entity.User;
import joinMe.db.exception.JoinRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class JoinRequestService {
    private final JoinRequestDao dao;

    @Autowired
    public JoinRequestService(JoinRequestDao dao) {
        this.dao = dao;
    }

    @Transactional
    public void persist(JoinRequest joinRequest) {
        Objects.requireNonNull(joinRequest);
        dao.persist(joinRequest);
    }

    @Transactional
    public JoinRequest findByID(Integer id) {
        return dao.find(id);
    }

    @Transactional
    public List<JoinRequest> findByRequester(User requester) {
        Objects.requireNonNull(requester);
        return dao.findByRequester(requester);
    }

    @Transactional
    public JoinRequest findByRequesterAndTrip(User requester, Trip trip) {
        Objects.requireNonNull(requester);
        return dao.findByRequesterAndTrip(requester, trip);
    }

    @Transactional
    public List<JoinRequest> getJoinRequestsForApproval(User author) {
        Objects.requireNonNull(author);
        return dao.getJoinRequestsForApproval(author);
    }

    @Transactional
    public JoinRequest create(User requester, Trip trip) {
        Objects.requireNonNull(requester);
        Objects.requireNonNull(trip);

        User tripCreator = trip.getAuthor();
        if (requester == tripCreator) {
            throw new JoinRequestException("User cannot create join request to the trip he/she is author of.");
        }

        JoinRequest existingRequest = dao.findByRequesterAndTrip(requester, trip);
        if (existingRequest != null && existingRequest.getStatus() != RequestStatus.REJECTED) {
            throw new JoinRequestException("User cannot create more than one join request to one trip.");
        }

        JoinRequest joinRequest = new JoinRequest();
        joinRequest.setRequester(requester);
        joinRequest.setTrip(trip);
        dao.persist(joinRequest);
        return joinRequest;
    }
}
