package joinMe.db.dao;

import jakarta.persistence.NoResultException;
import joinMe.db.entity.Trip;
import joinMe.db.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDao extends BaseDao<User>{
    public UserDao() {
        super(User.class);
    }

    public User findByUsername(String email) {
        try {
            return em.createNamedQuery("User.findByEmail", User.class).setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<User> getAllJoinersOfAttendlist(Trip trip) {
        try {
            return em.createNamedQuery("User.getAllJoinersOfAttendlist", User.class).setParameter("trip", trip)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
}
