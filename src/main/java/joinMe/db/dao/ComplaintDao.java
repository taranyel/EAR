package joinMe.db.dao;

import jakarta.persistence.NoResultException;
import joinMe.db.entity.Complaint;
import joinMe.db.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ComplaintDao extends BaseDao<Complaint> {
    public ComplaintDao() {
        super(Complaint.class);
    }

    public List<Complaint> findByAccused(User accused) {
        try {
            return em.createNamedQuery("Complaint.findByAccused", Complaint.class).setParameter("accused", accused)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
}
