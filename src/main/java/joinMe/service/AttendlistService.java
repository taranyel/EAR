package joinMe.service;

import jakarta.transaction.Transactional;
import joinMe.db.dao.AttendlistDao;
import joinMe.db.dao.MessageDao;
import joinMe.db.dao.TripDao;
import joinMe.db.dao.UserDao;
import joinMe.db.entity.Attendlist;
import joinMe.db.entity.Message;
import joinMe.db.entity.Trip;
import joinMe.db.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class AttendlistService {

    private final AttendlistDao attendlistDao;

    private final TripDao tripDao;

    private final UserDao userDao;

    private final MessageDao messageDao;

    @Autowired
    public AttendlistService(AttendlistDao attendlistDao, TripDao tripDao, UserDao userDao, MessageDao messageDao) {
        this.attendlistDao = attendlistDao;
        this.tripDao = tripDao;
        this.userDao = userDao;
        this.messageDao = messageDao;
    }

    public User getAdmin(Attendlist attendlist) {
        Objects.requireNonNull(attendlist);
        return attendlist.getTrip().getAuthor();
    }

    public Attendlist findByID(Integer id) {
        return attendlistDao.find(id);
    }

    public Attendlist create(User admin, Trip trip) {
        Attendlist attendlist = Attendlist.builder()
                .joiner(admin)
                .trip(trip)
                .build();

        attendlistDao.persist(attendlist);
        admin.addAttendlist(attendlist);
        trip.addAttendlist(attendlist);

        userDao.update(admin);
        tripDao.update(trip);
        return attendlist;
    }

    public void addMessage(Attendlist attendlist, Message message) {
        Objects.requireNonNull(attendlist);
        Objects.requireNonNull(message);
        attendlist.addMessage(message);
        attendlistDao.update(attendlist);

        message.setAttendlist(attendlist);
        message.setAuthor(attendlist.getJoiner());
        messageDao.persist(message);
    }

    public Attendlist findByTripAndJoiner(Trip trip, User joiner) {
        Objects.requireNonNull(trip);
        Objects.requireNonNull(joiner);
        return attendlistDao.findByTripAndJoiner(trip, joiner);
    }

    public List<Attendlist> findByTrip(Trip trip) {
        Objects.requireNonNull(trip);
        return attendlistDao.findByTrip(trip);
    }
}
