package joinMe.service;

import jakarta.transaction.Transactional;
import joinMe.db.dao.WishlistDao;
import joinMe.db.entity.Trip;
import joinMe.db.entity.User;
import joinMe.db.entity.Wishlist;
import joinMe.db.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public Wishlist findByID(Integer id) {
        Wishlist wishlist = dao.find(id);
        if (wishlist == null) {
            throw NotFoundException.create("Wishlist", id);
        }
        return wishlist;
    }

    public Wishlist create(User user, Trip trip) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(trip);
        return Wishlist.builder()
                .owner(user)
                .trip(trip)
                .build();
    }

    public List<Trip> findWishlistByOwner(User user) {
        Objects.requireNonNull(user);
        return dao.findTripsByOwner(user);
    }
}
