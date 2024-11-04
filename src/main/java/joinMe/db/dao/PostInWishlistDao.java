package joinMe.db.dao;

import org.springframework.stereotype.Repository;

@Repository
public class PostInWishlistDao extends BaseDao<PostInWishlistDao> {
    public PostInWishlistDao() {
        super(PostInWishlistDao.class);
    }
}
