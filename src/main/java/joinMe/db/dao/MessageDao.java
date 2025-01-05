package joinMe.db.dao;

import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import joinMe.db.entity.Message;
import joinMe.db.entity.Trip;
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

    public List<Message> findByTrip(Trip trip) {
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Message> cq = cb.createQuery(Message.class);
            Root<Message> root = cq.from(Message.class);
            root.fetch("attendlist", JoinType.LEFT);

            cq.where(cb.equal(root.get("attendlist").get("trip").get("id"), trip.getId()))
                    .orderBy(cb.desc(root.get("time")));

            return em.createQuery(cq).getResultList();
        } catch (Exception e) {
            return null;
        }
    }
}
