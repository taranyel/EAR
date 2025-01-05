package joinMe.service;

import joinMe.db.dao.MessageDao;
import joinMe.db.entity.Message;
import joinMe.db.entity.Trip;
import joinMe.db.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class MessageService {

    private final MessageDao dao;

    @Autowired
    public MessageService(MessageDao dao) {
        this.dao = dao;
    }

    public void persist(Message message) {
        Objects.requireNonNull(message);
        dao.persist(message);
    }

    public Message findByID(Integer id) {
        Message message = dao.find(id);
        if (message == null) {
            throw NotFoundException.create("Message", id);
        }
        return message;
    }

    public List<Message> findByTrip(Trip trip) {
        Objects.requireNonNull(trip);
        return dao.findByTrip(trip);
    }
}
