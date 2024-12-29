package joinMe.service;

import jakarta.transaction.Transactional;
import joinMe.db.dao.AttendlistDao;
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

    @Autowired
    public AttendlistService(AttendlistDao dao) {
        this.dao = dao;
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
        Attendlist attendlist = new Attendlist(admin, trip);
        admin.addAttendlist(attendlist);
        dao.persist(attendlist);
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
