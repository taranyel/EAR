package joinMe.db.dao;

import jakarta.persistence.NoResultException;
import joinMe.db.entity.Trip;
import joinMe.db.entity.TripStatus;
import joinMe.db.entity.User;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
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

    public List<Trip> findByCountry(String country) {
        try {
            return em.createNamedQuery("Trip.findByCountry", Trip.class).setParameter("country", country)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Trip> findByStartDate(LocalDate startDate) {
        try {
            return em.createNamedQuery("Trip.findByStartDate", Trip.class).setParameter("startDate", startDate)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Trip> findByEndDate(Date endDate) {
        try {
            return em.createNamedQuery("Trip.findByEndDate", Trip.class).setParameter("endDate", endDate)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Trip> findByCapacity(Integer capacity) {
        try {
            return em.createNamedQuery("Trip.findByCapacity", Trip.class).setParameter("capacity", capacity)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Trip> findByAuthor(User author) {
        try {
            return em.createNamedQuery("Trip.findByAuthor", Trip.class).setParameter("author", author)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
}
