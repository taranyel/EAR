package joinMe.dao;

import joinMe.Application;
import joinMe.db.dao.MessageDao;
import joinMe.db.entity.Attendlist;
import joinMe.db.entity.Message;
import joinMe.db.entity.Trip;
import joinMe.db.entity.User;
import joinMe.environment.Generator;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;

import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ComponentScan(basePackageClasses = Application.class)
public class MessageDaoTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private MessageDao messageDao;

    @Test
    public void findByCountryReturnsCorrectCountryById() {
        User user = Generator.generateUser();
        em.persist(user.getAddress());
        em.persist(user);

        Trip trip = Generator.generateTrip(user);
        em.persist(trip);
        user.addTrip(trip);

        Attendlist attendlist = new Attendlist();
        attendlist.setTrip(trip);
        attendlist.setJoiner(user);
        List<Message> messages = new ArrayList<>();
        Message message = new Message();
        message.setText("Hello world!");
        message.setAuthor(user);
        message.setAttendlist(attendlist);
        messages.add(message);
        attendlist.setMessages(messages);
        em.persist(attendlist);
        em.persist(message);

        final Message result = messageDao.findByAuthor(user).get(0);
        assertNotNull(result);
        assertEquals(message, result);
    }
}
