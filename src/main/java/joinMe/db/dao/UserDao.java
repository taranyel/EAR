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

    public User findByUsername(String username) {
        try {
            return em.createNamedQuery("User.findByUsername", User.class).setParameter("username", username)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public User findByEmail(String email) {
        try {
            return em.createNamedQuery("User.findByEmail", User.class).setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<User> getAllJoinersOfAttendlistByTrip(Trip trip) {
        try {
            return em.createNamedQuery("User.getAllJoinersOfAttendlistByTrip", User.class).setParameter("trip", trip)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<User> getAllJoinersOfAttendlistByID(Integer id) {
        try {
            return em.createNamedQuery("User.getAllJoinersOfAttendlistById", User.class).setParameter("id", id)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
}
