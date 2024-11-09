package joinMe.service;

import jakarta.transaction.Transactional;
import joinMe.db.dao.CommentDao;
import joinMe.db.entity.Comment;
import joinMe.db.entity.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class CommentService {
    private final CommentDao dao;

    @Autowired
    public CommentService(CommentDao dao) {
        this.dao = dao;
    }

    public void persist(Comment comment) {
        Objects.requireNonNull(comment);
        dao.persist(comment);
    }

    public List<Comment> findByTrip(Trip trip) {
        return dao.findByTrip(trip);
    }
}
