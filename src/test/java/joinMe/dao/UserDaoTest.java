package joinMe.dao;

import joinMe.Application;
import joinMe.db.dao.UserDao;
import joinMe.db.entity.User;
import joinMe.environment.Generator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ComponentScan(basePackageClasses = Application.class)
public class UserDaoTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private UserDao userDao;

    @Test
    public void findByUsernameReturnsPersonWithMatchingUsername() {
        final User user = Generator.generateUser();
        em.persist(user.getAddress());
        em.persist(user);

        final User result = userDao.findByUsername(user.getUsername());
        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
    }

    public void findByUsernameReturnsNullForUnknownUsername() {
        assertNull(userDao.findByUsername("unknownUsername"));
    }
}
