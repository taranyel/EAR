package joinMe.db.dao;

import jakarta.persistence.NoResultException;
import joinMe.db.entity.JoinRequest;
import joinMe.db.entity.Trip;
import joinMe.db.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JoinRequestDao extends BaseDao<JoinRequest> {

    protected JoinRequestDao() {
        super(JoinRequest.class);
    }

    public List<JoinRequest> findByRequester(User requester) {
        try {
            return em.createNamedQuery("JoinRequest.findByRequester", JoinRequest.class).setParameter("requester", requester)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    public JoinRequest findByRequesterAndTrip(User requester, Trip trip) {
        try {
            return em.createNamedQuery("JoinRequest.findByRequesterAndTrip", JoinRequest.class).setParameter("requester", requester).setParameter("trip", trip)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<JoinRequest> getJoinRequestsForApproval(User author) {
        try {
            return em.createNamedQuery("JoinRequest.getJoinRequestsForApproval", JoinRequest.class).setParameter("author", author)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
}
