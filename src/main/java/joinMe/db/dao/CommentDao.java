package joinMe.db.dao;

import jakarta.persistence.NoResultException;
import joinMe.db.entity.Comment;
import joinMe.db.entity.Message;
import joinMe.db.entity.Trip;
import joinMe.db.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CommentDao extends BaseDao<Comment> {
    public CommentDao() {
        super(Comment.class);
    }

    public List<Comment> findByTrip(Trip trip) {
        try {
            return em.createNamedQuery("Comment.findByTrip", Comment.class).setParameter("trip", trip)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
}
