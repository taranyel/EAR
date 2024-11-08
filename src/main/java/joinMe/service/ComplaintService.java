package joinMe.service;

import joinMe.db.dao.ComplaintDao;
import joinMe.db.entity.Complaint;
import joinMe.db.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class ComplaintService {
    private final ComplaintDao dao;

    @Autowired
    public ComplaintService(ComplaintDao dao) {
        this.dao = dao;
    }

    public void persist(Complaint complaint) {
        Objects.requireNonNull(complaint);
        dao.persist(complaint);
    }

    public List<Complaint> findByAccused(User accused) {
        return dao.findByAccused(accused);
    }
}
