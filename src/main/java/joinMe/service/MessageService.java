package joinMe.service;

import joinMe.db.dao.MessageDao;
import joinMe.db.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return dao.find(id);
    }
}
