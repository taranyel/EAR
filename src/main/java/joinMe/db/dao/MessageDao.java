package joinMe.db.dao;

import jakarta.persistence.NoResultException;
import joinMe.db.entity.Attendlist;
import joinMe.db.entity.Message;
import joinMe.db.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MessageDao extends BaseDao<Message> {
    public MessageDao() {
        super(Message.class);
    }

    public List<Message> findByAuthor(User author) {
        try {
            return em.createNamedQuery("Message.findByAuthor", Message.class).setParameter("author", author)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Message> findByAttendList(Attendlist attendlist) {
        try {
            return em.createNamedQuery("Message.findByAttendList", Message.class).setParameter("attendlist", attendlist)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
}
