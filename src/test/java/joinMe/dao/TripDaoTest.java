package joinMe.dao;

import joinMe.Application;
import joinMe.db.dao.TripDao;
import joinMe.db.entity.Trip;
import joinMe.db.entity.User;
import joinMe.db.entity.Wishlist;
import joinMe.environment.Generator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ComponentScan(basePackageClasses = Application.class)
public class TripDaoTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private TripDao tripDao;

    private User user;

    private Trip trip;

    public void createUserTripAndPersist() {
        user = Generator.generateUser(em);
        em.persist(user.getAddress());
        em.persist(user);

        trip = Generator.generateTrip(user, em);
        em.persist(trip);
    }

    public void addMultipleWishlistsToList(List<Wishlist> list) {
        for (Wishlist wishlist : list) {
            user.addWishlist(wishlist);
        }
    }

    @Test
    public void findByCountryReturnsCorrectCountryById() {
        createUserTripAndPersist();
        final List<Trip> result = tripDao.findByCountry(trip.getCountry());
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(trip, result.get(0));
    }

    @Test
    public void findByAuthor() {
        createUserTripAndPersist();
        final Trip result = tripDao.findByAuthor(user).get(0);

        assertEquals(trip, result);
    }

    @Test
    public void findByStartDate() {
        createUserTripAndPersist();

        final List<Trip> notFound = tripDao.findByStartDate(new Date());
        final Trip result = tripDao.findByStartDate(trip.getStartDate()).get(0);

        assertEquals(trip, result);
        assertTrue(notFound.isEmpty());
    }

    //TODO
    @Test
    public void findByCountry() {

    }

    //TODO
    @Test
    public void findByEndDate() {

    }

    //TODO
    @Test
    public void findByCapacity() {

    }

    //TODO
    @Test
    public void findInWishlistByOwner() {

    }
}
