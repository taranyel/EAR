package joinMe.db.dao;

import joinMe.db.entity.Comment;
import org.springframework.stereotype.Repository;

@Repository
public class CommentDao extends BaseDao<Comment> {
    public CommentDao() {
        super(Comment.class);
    }
}
