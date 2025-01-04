package joinMe.db.dao;

import jakarta.persistence.NoResultException;
import joinMe.db.entity.Trip;
import joinMe.db.entity.TripStatus;
import joinMe.db.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TripDao extends BaseDao<Trip> {
    public TripDao() {
        super(Trip.class);
    }

    public List<Trip> findByStatus(TripStatus status) {
        try {
            return em.createNamedQuery("Trip.findByStatus", Trip.class).setParameter("status", status)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Trip> findByAuthor(User user) {
        try {
            return em.createNamedQuery("Trip.findByAuthor", Trip.class).setParameter("author", user)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
}
