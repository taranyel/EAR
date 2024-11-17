package joinMe.service;

import jakarta.transaction.Transactional;
import joinMe.db.dao.AttendlistDao;
import joinMe.db.dao.CommentDao;
import joinMe.db.dao.TripDao;
import joinMe.db.dao.UserDao;
import joinMe.db.entity.Comment;
import joinMe.db.entity.Trip;
import joinMe.db.entity.User;
import joinMe.environment.CommonMethods;
import joinMe.environment.Generator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Transactional
@AutoConfigureTestEntityManager
@ActiveProfiles("test")
public class TripServiceTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private TripService tripService;

    @Autowired UserService userService;

    @SpyBean
    private UserDao userDao;

    @SpyBean
    private AttendlistDao attendlistDao;

    @SpyBean
    private CommentDao commentDao;

    @SpyBean
    private TripDao tripDao;

    private User user;

    private Trip trip;

    @BeforeEach
    public void setUp() {
        user = Generator.generateUser(em);
        trip = Generator.generateTrip(user, em);
        CommonMethods.addTripIntoTripList(user, trip, 0, userService, attendlistDao, userDao);
    }

    public void addCommentIntoCommentList(Trip trip, Comment comment, int value) {
        tripService.addComment(trip, comment);
        verify(tripDao, times(value + 1)).update(trip);
    }

    public void addCommentListIntoCommentList(Trip trip, List<Comment> commentList) {
        for (int i = 0; i < commentList.size(); i++) {
            addCommentIntoCommentList(trip, commentList.get(i), i);
        }
    }

    @Test
    public void addCommentTest() {
        Comment comment = CommonMethods.createCommentAndPersist(em, user, trip);

        addCommentIntoCommentList(trip, comment, 0);
        String commentText = comment.getText();

        final Trip result = em.find(Trip.class, trip.getId());
        assertEquals(1, result.getComments().size());
        assertEquals(commentText, trip.getComments().get(0).getText());
    }

    @Test
    public void addCommentMultipleCommentsTest() {
        User follower = Generator.generateUser(em);

        Comment comment = CommonMethods.createCommentAndPersist(em, user, trip);
        List<Comment> commentList = CommonMethods.createCommentsAndPersist(3, em, follower, trip);
        commentList.add(comment);

        addCommentListIntoCommentList(trip, commentList);

        final Trip result = em.find(Trip.class, trip.getId());
        assertIterableEquals(trip.getComments(), result.getComments());
    }

    @Test
    public void removeCommentTest() {
        Comment comment = CommonMethods.createCommentAndPersist(em, user, trip);

        addCommentIntoCommentList(trip, comment, 0);
        tripService.removeComment(trip, comment);

        final Trip result = em.find(Trip.class, trip.getId());
        assertTrue(result.getComments().isEmpty());
    }
}
