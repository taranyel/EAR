package joinMe.service;

import jakarta.transaction.Transactional;
import joinMe.db.dao.AttendlistDao;
import joinMe.db.entity.Attendlist;
import joinMe.db.entity.Trip;
import joinMe.db.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class AttendlistService {

    private final AttendlistDao dao;

    @Autowired
    public AttendlistService(AttendlistDao dao) {
        this.dao = dao;
    }

    @Transactional
    public void persist(Attendlist attendlist) {
        Objects.requireNonNull(attendlist);
        dao.persist(attendlist);
    }

    public User getAdmin(Attendlist attendlist) {
        Objects.requireNonNull(attendlist);
        return attendlist.getTrip().getAuthor();
    }

    @Transactional
    public Attendlist findByTripAndJoiner(Trip trip, User joiner) {
        return dao.findByTripAndJoiner(trip, joiner);
    }

    @Transactional
    public List<Attendlist> findByJoiner(User joiner) {
        return dao.findByJoiner(joiner);
    }
}
