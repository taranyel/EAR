package joinMe.db.dao;

import jakarta.persistence.NoResultException;
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

    public List<User> findByRole(String role) {
        try {
            return em.createQuery("SELECT u FROM User u WHERE u.role = :role", User.class).setParameter("role", role).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
}
