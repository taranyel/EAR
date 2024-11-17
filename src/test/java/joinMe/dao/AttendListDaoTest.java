package joinMe.dao;

import joinMe.Application;
import joinMe.db.dao.TripDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;

@DataJpaTest
@ComponentScan(basePackageClasses = Application.class)
public class AttendListDaoTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private TripDao tripDao;

    //TODO
    @Test
    public void findByTripAndJoinerTest() {

    }

    //TODO
    @Test
    public void findByJoiner() {

    }
}
