package joinMe.db.dao;

import jakarta.persistence.NoResultException;
import joinMe.db.entity.Post;
import org.springframework.stereotype.Repository;

@Repository
public class PostDao extends BaseDao<Post> {
    public PostDao() {
        super(Post.class);
    }
    public Post findByCountry(String country) {
        try {
            return em.createNamedQuery("Post.findByCountry", Post.class).setParameter("country", country)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
