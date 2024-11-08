package joinMe.db.dao;

import joinMe.db.entity.Wishlist;
import org.springframework.stereotype.Repository;

@Repository
public class WishlistDao extends BaseDao<Wishlist> {
    public WishlistDao() {
        super(Wishlist.class);
    }
}
