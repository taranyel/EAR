package joinMe.db.dao;

import jakarta.persistence.NoResultException;
import joinMe.db.entity.Trip;
import joinMe.db.entity.TripStatus;
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
}
