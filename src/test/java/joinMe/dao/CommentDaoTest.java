package joinMe.dao;

import joinMe.Application;
import joinMe.db.dao.CommentDao;
import joinMe.db.entity.Comment;
import joinMe.db.entity.Trip;
import joinMe.db.entity.User;
import joinMe.environment.CommonMethods;
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
public class CommentDaoTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private CommentDao commentDao;

    private List<Trip> trips;

    private List<Comment> comments;

    public void addComment() {
        User user = Generator.generateUser(em);
        trips = Generator.generateTrips(2, user, em);
        comments = CommonMethods.createCommentsAndPersist(2, em, user, trips.get(0));

        trips.get(0).addComment(comments.get(0));
        trips.get(1).addComment(comments.get(1));
    }

    @Test
    public void findByTripTest() {
        addComment();

        final Comment result1 = commentDao.findByTrip(trips.get(0)).get(0);
        final Comment result2 = commentDao.findByTrip(trips.get(1)).get(0);
        assertEquals(comments.get(0), result1);
        assertEquals(comments.get(1), result2);
    }
}
