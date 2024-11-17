package joinMe.environment;

import joinMe.db.dao.AttendlistDao;
import joinMe.db.dao.UserDao;
import joinMe.db.entity.Comment;
import joinMe.db.entity.Trip;
import joinMe.db.entity.User;
import joinMe.service.UserService;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class CommonMethods {
    public static void addTripIntoTripList(User us, Trip trip, int i, UserService userService, AttendlistDao attendlistDao, UserDao userDao) {
        userService.addTrip(us, trip);
        verify(attendlistDao).persist(us.getAttendlists().get(i));
        verify(userDao, times(i + 1)).update(us);
    }

    public static void addTripListIntoTripList(User us, List<Trip> trips, UserService userService, AttendlistDao attendlistDao, UserDao userDao) {
        for (int i = 0; i < trips.size(); i++) {
            addTripIntoTripList(us, trips.get(i), i, userService, attendlistDao, userDao);
        }
    }

    public static Comment createCommentAndPersist(TestEntityManager em, User author, Trip trip) {
        Comment comment = new Comment();
        comment.setAuthor(author);
        comment.setTrip(trip);
        comment.setText(Generator.randomString());
        em.persist(comment);

        return comment;
    }

    public static List<Comment> createCommentsAndPersist(int amount, TestEntityManager em, User author, Trip trip) {
        List<Comment> buffer = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            buffer.add(createCommentAndPersist(em, author, trip));
        }
        return buffer;
    }
}
