package joinMe.service;

import jakarta.transaction.Transactional;
import joinMe.db.dao.AttendlistDao;
import joinMe.db.dao.TripDao;
import joinMe.db.dao.UserDao;
import joinMe.db.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class TripService {

    private final TripDao tripDao;

    private final UserDao userDao;

    private final AttendlistDao attendlistDao;

    @Autowired
    public TripService(TripDao tripDao, UserDao userDao, AttendlistDao attendlistDao) {
        this.tripDao = tripDao;
        this.userDao = userDao;
        this.attendlistDao = attendlistDao;
    }

    @Transactional
    public void persist(Trip trip) {
        Objects.requireNonNull(trip);
        tripDao.persist(trip);
    }

    @Transactional
    public void remove(Trip trip) {
        Objects.requireNonNull(trip);

        List<User> joiners = userDao.getAllJoinersOfAttendlistByTrip(trip);
        for (User joiner : joiners) {
            Attendlist attendlist = attendlistDao.findByTripAndJoiner(trip, joiner);
            joiner.removeAttendlist(attendlist);
            userDao.update(joiner);
            attendlistDao.remove(attendlist);
        }

        tripDao.remove(trip);
    }

    @Transactional
    public void addComment(Trip trip, Comment comment) {
        Objects.requireNonNull(trip);
        Objects.requireNonNull(comment);
        trip.addComment(comment);
        tripDao.update(trip);
    }

    @Transactional
    public void removeComment(Trip trip, Comment comment) {
        Objects.requireNonNull(trip);
        Objects.requireNonNull(comment);
        trip.removeComment(comment);
        tripDao.update(trip);
    }

    @Transactional
    public List<Trip> findAllActiveTrips() {
        return tripDao.findByStatus(TripStatus.ACTIVE);
    }

    @Transactional
    public List<Trip> findByCountry(String country) {
        Objects.requireNonNull(country);
        return tripDao.findByCountry(country);
    }

    @Transactional
    public List<Trip> findByStartDate(Date startDate) {
        Objects.requireNonNull(startDate);
        return tripDao.findByStartDate(startDate);
    }

    @Transactional
    public List<Trip> findByEndDate(Date endDate) {
        Objects.requireNonNull(endDate);
        return tripDao.findByEndDate(endDate);
    }

    @Transactional
    public List<Trip> findByCapacity(Integer capacity) {
        Objects.requireNonNull(capacity);
        return tripDao.findByCapacity(capacity);
    }

    @Transactional
    public List<Trip> findByAuthor(User author) {
        Objects.requireNonNull(author);
        return tripDao.findByAuthor(author);
    }

    @Transactional
    public Trip findByID(Integer id) {
        return tripDao.find(id);
    }

    @Transactional
    public List<Trip> findInWishlistByOwner(User owner) {
        Objects.requireNonNull(owner);
        return tripDao.findInWishlistByOwner(owner);
    }

    public User getAuthor(Trip trip) {
        Objects.requireNonNull(trip);
        return trip.getAuthor();
    }
}
