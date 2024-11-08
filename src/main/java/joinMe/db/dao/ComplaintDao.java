package joinMe.db.dao;

import joinMe.db.entity.Complaint;
import org.springframework.stereotype.Repository;

@Repository
public class ComplaintDao extends BaseDao<Complaint> {
    public ComplaintDao() {
        super(Complaint.class);
    }
}
