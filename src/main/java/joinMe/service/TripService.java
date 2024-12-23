package joinMe.service;

import jakarta.transaction.Transactional;
import joinMe.db.dao.TripDao;
import joinMe.db.entity.Comment;
import joinMe.db.entity.Trip;
import joinMe.db.entity.TripStatus;
import joinMe.db.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class TripService {

    private final TripDao dao;

    @Autowired
    public TripService(TripDao dao) {
        this.dao = dao;
    }

    @Transactional
    public void persist(Trip trip) {
        Objects.requireNonNull(trip);
        dao.persist(trip);
    }

    @Transactional
    public void addComment(Trip trip, Comment comment) {
        Objects.requireNonNull(trip);
        Objects.requireNonNull(comment);
        trip.addComment(comment);
        dao.update(trip);
    }

    @Transactional
    public void removeComment(Trip trip, Comment comment) {
        Objects.requireNonNull(trip);
        Objects.requireNonNull(comment);
        trip.removeComment(comment);
        dao.update(trip);
    }

    @Transactional
    public List<Trip> findAllActiveTrips() {
        return dao.findByStatus(TripStatus.ACTIVE);
    }

    @Transactional
    public List<Trip> findByCountry(String country) {
        Objects.requireNonNull(country);
        return dao.findByCountry(country);
    }

    @Transactional
    public List<Trip> findByStartDate(Date startDate) {
        Objects.requireNonNull(startDate);
        return dao.findByStartDate(startDate);
    }

    @Transactional
    public List<Trip> findByEndDate(Date endDate) {
        Objects.requireNonNull(endDate);
        return dao.findByEndDate(endDate);
    }

    @Transactional
    public List<Trip> findByCapacity(Integer capacity) {
        Objects.requireNonNull(capacity);
        return dao.findByCapacity(capacity);
    }

    @Transactional
    public List<Trip> findByAuthor(User author) {
        Objects.requireNonNull(author);
        return dao.findByAuthor(author);
    }

    @Transactional
    public Trip findByID(Integer id) {
        return dao.find(id);
    }

    @Transactional
    public List<Trip> findInWishlistByOwner(User owner) {
        Objects.requireNonNull(owner);
        return dao.findInWishlistByOwner(owner);
    }

    public User getAuthor(Trip trip) {
        Objects.requireNonNull(trip);
        return trip.getAuthor();
    }
}
