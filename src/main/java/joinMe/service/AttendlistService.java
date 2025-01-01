package joinMe.service;

import jakarta.transaction.Transactional;
import joinMe.db.dao.AttendlistDao;
import joinMe.db.dao.TripDao;
import joinMe.db.dao.UserDao;
import joinMe.db.entity.Attendlist;
import joinMe.db.entity.Message;
import joinMe.db.entity.Trip;
import joinMe.db.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AttendlistService {

    private final AttendlistDao dao;

    private final TripDao tripDao;

    private final UserDao userDao;

    @Autowired
    public AttendlistService(AttendlistDao dao, TripDao tripDao, UserDao userDao) {
        this.dao = dao;
        this.tripDao = tripDao;
        this.userDao = userDao;
    }

    public User getAdmin(Attendlist attendlist) {
        Objects.requireNonNull(attendlist);
        return attendlist.getTrip().getAuthor();
    }

    @Transactional
    public Attendlist findByID(Integer id) {
        return dao.find(id);
    }

    @Transactional
    public Attendlist create(User admin, Trip trip) {
        Attendlist attendlist = Attendlist.builder()
                .joiner(admin)
                .trip(trip)
                .build();
        admin.addAttendlist(attendlist);
        trip.addAttendlist(attendlist);
        dao.persist(attendlist);
        userDao.update(admin);
        tripDao.update(trip);
        return attendlist;
    }

    @Transactional
    public void addMessage(Attendlist attendlist, Message message) {
        Objects.requireNonNull(attendlist);
        Objects.requireNonNull(message);
        attendlist.addMessage(message);
        dao.update(attendlist);
    }
}
