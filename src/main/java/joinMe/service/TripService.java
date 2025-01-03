package joinMe.service;

import jakarta.transaction.Transactional;
import joinMe.db.dao.AttendlistDao;
import joinMe.db.dao.TripDao;
import joinMe.db.dao.UserDao;
import joinMe.db.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public void update(Trip current, Trip newTrip) {
        Objects.requireNonNull(current);
        Objects.requireNonNull(newTrip);

        current.setCountry(newTrip.getCountry());
        current.setDescription(newTrip.getDescription());
        current.setStartDate(newTrip.getStartDate());
        current.setEndDate(newTrip.getEndDate());
        current.setCapacity(newTrip.getCapacity());
        current.setImagePath(newTrip.getImagePath());
        current.setTitle(newTrip.getTitle());

        tripDao.update(current);
    }

    @Transactional
    public void persist(Trip trip) {
        Objects.requireNonNull(trip);
        tripDao.persist(trip);
    }

    @Transactional
    public boolean update(Integer id, Trip trip) {
        if (tripDao.exists(id)) {
            Trip tripOrigin = tripDao.find(id);
            trip.setAuthor(tripOrigin.getAuthor());
            trip.setAttendlists(tripOrigin.getAttendlists());
            trip.setComments(tripOrigin.getComments());
            tripDao.update(trip);
            return true;
        }
        return false;
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
    public Trip findByID(Integer id) {
        return tripDao.find(id);
    }
}
