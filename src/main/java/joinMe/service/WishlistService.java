package joinMe.service;

import jakarta.transaction.Transactional;
import joinMe.db.dao.WishlistDao;
import joinMe.db.entity.Wishlist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Transactional
public class WishlistService {

    private final WishlistDao dao;

    @Autowired
    public WishlistService(WishlistDao dao) {
        this.dao = dao;
    }

    public void persist(Wishlist wishlist) {
        Objects.requireNonNull(wishlist);
        dao.persist(wishlist);
    }
}
