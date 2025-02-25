package joinMe.service;

import jakarta.transaction.Transactional;
import joinMe.db.dao.AttendlistDao;
import joinMe.db.dao.JoinRequestDao;
import joinMe.db.dao.TripDao;
import joinMe.db.dao.UserDao;
import joinMe.db.entity.*;
import joinMe.db.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class TripService {

    private final TripDao tripDao;

    private final UserDao userDao;

    private final AttendlistDao attendlistDao;

    private final JoinRequestDao joinRequestDao;

    @Autowired
    public TripService(TripDao tripDao, UserDao userDao, AttendlistDao attendlistDao, JoinRequestDao joinRequestDao) {
        this.tripDao = tripDao;
        this.userDao = userDao;
        this.attendlistDao = attendlistDao;
        this.joinRequestDao = joinRequestDao;
    }

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

    public void persist(Trip trip) {
        Objects.requireNonNull(trip);
        tripDao.persist(trip);
    }

    public void remove(Trip trip) {
        Objects.requireNonNull(trip);

        List<Attendlist> attendlists = attendlistDao.findByTrip(trip);
        for (Attendlist attendlist : attendlists) {
            attendlist.getJoiner().removeAttendlist(attendlist);
            userDao.update(attendlist.getJoiner());
        }

        List<JoinRequest> joinRequests = joinRequestDao.findByTrip(trip);
        for (JoinRequest joinRequest : joinRequests) {
            joinRequest.getRequester().removeJoinRequest(joinRequest);
            userDao.update(joinRequest.getRequester());
        }

        tripDao.remove(trip);
    }

    public void addComment(Trip trip, Comment comment) {
        Objects.requireNonNull(trip);
        Objects.requireNonNull(comment);
        trip.addComment(comment);
        comment.setTrip(trip);
        tripDao.update(trip);
    }

    public void removeAttendlist(Trip trip, Attendlist attendlist) {
        Objects.requireNonNull(trip);
        Objects.requireNonNull(attendlist);
        trip.removeAttendlist(attendlist);
        tripDao.update(trip);
    }

    public void removeComment(Trip trip, Comment comment) {
        Objects.requireNonNull(trip);
        Objects.requireNonNull(comment);
        trip.removeComment(comment);
        tripDao.update(trip);
    }

    public List<Trip> findAllActiveTrips() {
        return tripDao.findByStatus(TripStatus.ACTIVE);
    }

    public List<Trip> findByAuthor(User user) {
        Objects.requireNonNull(user);
        return tripDao.findByAuthor(user);
    }

    public Trip findByID(Integer id) {
        Trip trip = tripDao.find(id);
        if (trip == null) {
            throw NotFoundException.create("Trip", id);
        }
        return trip;
    }
}
