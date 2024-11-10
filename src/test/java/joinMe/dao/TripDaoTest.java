package joinMe.dao;

import joinMe.Application;
import joinMe.db.dao.TripDao;
import joinMe.db.entity.Trip;
import joinMe.db.entity.User;
import joinMe.environment.Generator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ComponentScan(basePackageClasses = Application.class)
public class TripDaoTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private TripDao tripDao;

    @Test
    public void findByCountryReturnsCorrectCountryById() {
        User user = Generator.generateUser(em);
        em.persist(user.getAddress());
        em.persist(user);

        Trip trip = Generator.generateTrip(user, em);
        em.persist(trip);

        final List<Trip> result = tripDao.findByCountry(trip.getCountry());
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(trip, result.get(0));
    }
}
