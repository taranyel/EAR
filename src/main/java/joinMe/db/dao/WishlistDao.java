package joinMe.db.dao;

import jakarta.persistence.NoResultException;
import joinMe.db.entity.User;
import joinMe.db.entity.Wishlist;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class WishlistDao extends BaseDao<Wishlist> {
    public WishlistDao() {
        super(Wishlist.class);
    }
}
