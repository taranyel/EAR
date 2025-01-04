package joinMe.service;

import joinMe.db.dao.*;
import joinMe.db.entity.*;
import joinMe.db.exception.JoinRequestException;
import joinMe.environment.Generator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Transactional
@AutoConfigureTestEntityManager
@ActiveProfiles("test")
public class UserServiceTest {

    @Autowired
    private TestEntityManager em;

    @SpyBean
    private TripDao tripDao;

    @SpyBean
    private AddressDao addressDao;

    @SpyBean
    private UserDao userDao;

    @SpyBean
    private JoinRequestDao joinRequestDao;

    //    @Mock
    @SpyBean
    private AttendlistDao attendlistDao;

    @Autowired
    private UserService userService;

    private User user;

    private Trip trip;

    @BeforeEach
    public void setUp() {
        user = Generator.generateUser(em);
    }

    public void addTripIntoListByTrip(User us, Trip trip, int i) {
        userService.addTrip(us, trip);
        verify(attendlistDao).persist(us.getAttendlists().get(i));
        verify(userDao, times(i + 1)).update(us);
    }

    public void addTripsIntoListByList(User us, List<Trip> trips) {
        for (int i = 0; i < trips.size(); i++) {
            addTripIntoListByTrip(us, trips.get(i), i);
        }
    }

    public void addRequestApproveRequestForUser(User us, Trip trip) {
        JoinRequest joinRequest = new JoinRequest();
        joinRequest.setRequester(us);
        joinRequest.setTrip(trip);
        userService.addJoinRequest(joinRequest);
        verify(joinRequestDao).persist(joinRequest);
        verify(userDao).update(us);
        userService.approveJoinRequest(joinRequest);
        verify(attendlistDao).persist(us.getAttendlists().get(0));
        verify(joinRequestDao).update(joinRequest);
        verify(userDao, times(2)).update(joinRequest.getRequester());
    }

    public void addRequestApproveRequestForUsersList(List<User> users, Trip trip) {
        for (User value : users) {
            addRequestApproveRequestForUser(value, trip);
        }
    }

    @Test
    public void addTripCreatesAttendlistAndAddsTripCreatorAutomatically() {
        trip = Generator.generateTrips(1, user, em).get(0);

        addTripIntoListByTrip(user, trip, 0);

        final User result = em.find(User.class, user.getId());
        assertEquals(user.getAttendlists(), result.getAttendlists());
    }

    @Test
    public void addTripCreatesAttendlistAndAddsTripCreatorAutomaticallyWithMoreTrips() {
        List<Trip> trips = Generator.generateTrips(3, user, em);

        addTripsIntoListByList(user, trips);

        final User result = em.find(User.class, user.getId());
        assertEquals(user.getAttendlists(), result.getAttendlists());
        assertEquals(user.getTrips(), result.getTrips());
    }

//    @Test
//    public void removeTripRemovesAllUsersInAttendlist() {
//        List<User> toRemoveUsers = Generator.generateUsers(2, em);
//        trip = Generator.generateTrip(user, em);
//
//        addTripIntoListByTrip(user, trip, 0);
//        addRequestApproveRequestForUsersList(toRemoveUsers, trip);
//
//        userService.removeTrip(user, trip);
//
//        final List<User> resultList = em.getEntityManager().createQuery("SELECT u FROM User u", User.class).getResultList();
//        for (User result : resultList) {
//            assertEquals(0, result.getAttendlists().size());
//            if (result.isAdmin()) {
//                assertEquals(0, result.getTrips().size());
//            }
//        }
//    }

    @Test
    public void approveRequestAddsUserIntoAttendlist() {
        List<User> toRemoveUsers = Generator.generateUsers(2, em);
        trip = Generator.generateTrip(user, em);

        addTripIntoListByTrip(user, trip, 0);
        addRequestApproveRequestForUsersList(toRemoveUsers, trip);


        final List<User> resultList = em.getEntityManager().createQuery("SELECT u FROM User u", User.class).getResultList();
        for (User result : resultList) {
            assertEquals(1, result.getAttendlists().size());
        }
    }

    @Test
    public void addJoinRequestThrowsJoinRequestException() {
        User toAddUser = Generator.generateUser(em);
        trip = Generator.generateTrip(user, em);

        addTripIntoListByTrip(user, trip, 0);
        addRequestApproveRequestForUser(toAddUser, trip);


        JoinRequest joinRequest = new JoinRequest();
        joinRequest.setRequester(toAddUser);
        joinRequest.setTrip(trip);

        Exception exception = assertThrows(JoinRequestException.class, () -> {
            userService.addJoinRequest(joinRequest);
        });
        assertEquals("User cannot create more than one join request to one trip.", exception.getMessage());
    }

    @Test
    public void addJoinRequestForRejectedRegularUser() {
        User toAddUser = Generator.generateUser(em);
        trip = Generator.generateTrip(user, em);

        addTripIntoListByTrip(user, trip, 0);

        JoinRequest joinRequest = new JoinRequest();
        joinRequest.setRequester(toAddUser);
        joinRequest.setTrip(trip);
        joinRequest.setStatus(RequestStatus.REJECTED);
        em.persist(joinRequest);

        addRequestApproveRequestForUser(toAddUser, trip);

        final List<User> resultList = em.getEntityManager().createQuery("SELECT u FROM User u", User.class).getResultList();
        for (User result : resultList) {
            assertEquals(1, result.getAttendlists().size());
            if (!result.getTrips().isEmpty()) {
                assertEquals(user.getAttendlists(), result.getAttendlists());
            } else {
                assertEquals(toAddUser.getAttendlists(), result.getAttendlists());
            }
        }
    }

    @Test
    public void addJoinRequestForCreatorThrowsJoinRequestException() {
        trip = Generator.generateTrip(user, em);

        JoinRequest joinRequest = new JoinRequest();
        joinRequest.setRequester(user);
        joinRequest.setTrip(trip);
        em.persist(joinRequest);

        Exception exception = assertThrows(JoinRequestException.class, () -> {
            userService.addJoinRequest(joinRequest);
        });
        assertEquals("User cannot create join request to the trip he is author of.", exception.getMessage());
    }

    @Test
    public void approveJoinRequestWithMandatoryFieldsMissing() {
        User us = Generator.generateUser(em);
        trip = Generator.generateTrip(us, em);

        addTripIntoListByTrip(user, trip, 0);

        JoinRequest joinRequest = new JoinRequest();
        joinRequest.setRequester(us);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.addJoinRequest(joinRequest);
        });
        assertEquals("Fields in joinRequest can't be null!", exception.getMessage());
    }
}
