package joinMe.service;

import jakarta.transaction.Transactional;
import joinMe.db.dao.CommentDao;
import joinMe.db.entity.Comment;
import joinMe.db.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public Comment findByID(Integer id) {
        Comment comment = dao.find(id);
        if (comment == null) {
            throw NotFoundException.create("Comment", id);
        }
        return comment;
    }
}
