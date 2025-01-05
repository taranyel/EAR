package joinMe.service;

import jakarta.transaction.Transactional;
import joinMe.db.dao.AttendlistDao;
import joinMe.db.dao.TripDao;
import joinMe.db.dao.UserDao;
import joinMe.db.entity.*;
import joinMe.db.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class AttendlistService {

    private final AttendlistDao attendlistDao;

    private final TripDao tripDao;

    private final UserDao userDao;

    @Autowired
    public AttendlistService(AttendlistDao attendlistDao, TripDao tripDao, UserDao userDao) {
        this.attendlistDao = attendlistDao;
        this.tripDao = tripDao;
        this.userDao = userDao;
    }

    public Attendlist findByID(Integer id) {
        Attendlist attendlist = attendlistDao.find(id);
        if (attendlist == null) {
            throw NotFoundException.create("Attendlist", id);
        }
        return attendlist;
    }

    public Attendlist create(User admin, Trip trip) {
        Attendlist attendlist = Attendlist.builder()
                .joiner(admin)
                .trip(trip)
                .build();

        admin.addAttendlist(attendlist);
        trip.addAttendlist(attendlist);

        tripDao.update(trip);
        userDao.update(admin);
        return attendlist;
    }

    public void addMessage(Attendlist attendlist, Message message) {
        Objects.requireNonNull(attendlist);
        Objects.requireNonNull(message);

        message.setAttendlist(attendlist);
        message.setAuthor(attendlist.getJoiner());

        attendlist.addMessage(message);
        attendlistDao.update(attendlist);
    }

    public void removeMessage(Attendlist attendlist, Message message) {
        Objects.requireNonNull(attendlist);
        Objects.requireNonNull(message);
        attendlist.removeMessage(message);
        attendlistDao.update(attendlist);
    }

    public Attendlist findByTripAndJoiner(Trip trip, User joiner) {
        Objects.requireNonNull(trip);
        Objects.requireNonNull(joiner);
        return attendlistDao.findByTripAndJoiner(trip, joiner);
    }

    public List<Attendlist> findByJoiner(User user) {
        Objects.requireNonNull(user);
        return attendlistDao.findByJoiner(user);
    }

    public void remove(Attendlist attendlist) {
        Objects.requireNonNull(attendlist);
        attendlistDao.remove(attendlist);
    }

    public Attendlist isJoinerOfTrip(User user, Trip trip) {
        Attendlist attendlist = attendlistDao.findByTripAndJoiner(trip, user);
        if (user.getRole() != Role.ADMIN && attendlist == null) {
            throw new AccessDeniedException("User with id: " + user.getId() + " is not joiner of trip with id: " + trip.getId());
        }
        return attendlist;
    }
}
