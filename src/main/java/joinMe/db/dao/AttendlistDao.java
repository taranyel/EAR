package joinMe.db.dao;

import jakarta.persistence.NoResultException;
import joinMe.db.entity.Attendlist;
import joinMe.db.entity.Trip;
import joinMe.db.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AttendlistDao extends BaseDao<Attendlist> {
    public AttendlistDao() {
        super(Attendlist.class);
    }

    public Attendlist findByTripAndJoiner(Trip trip, User joiner) {
        try {
            return em.createNamedQuery("Attendlist.findByTripAndJoiner", Attendlist.class)
                    .setParameter("trip", trip).setParameter("joiner", joiner).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Attendlist> findByTrip(Trip trip) {
        try {
            return em.createNamedQuery("Attendlist.findByTrip", Attendlist.class).setParameter("trip", trip)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
}
