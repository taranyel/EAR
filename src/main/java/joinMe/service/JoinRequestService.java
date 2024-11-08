package joinMe.service;

import jakarta.transaction.Transactional;
import joinMe.db.dao.JoinRequestDao;
import joinMe.db.entity.JoinRequest;
import joinMe.db.entity.Trip;
import joinMe.db.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
