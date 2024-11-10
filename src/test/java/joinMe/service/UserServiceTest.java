package joinMe.service;

import joinMe.db.dao.*;
import joinMe.db.entity.*;
import joinMe.db.exception.JoinRequestException;
import joinMe.environment.Generator;
import org.hibernate.PropertyValueException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Transactional
@AutoConfigureTestEntityManager
@ActiveProfiles("test")
public class UserServiceTest {
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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
        user = Generator.generateUser();
        trip = Generator.generateTrip(user);
        em.persist(user.getAddress());
        em.persist(user);
        em.persist(trip);
        addressDao.persist(user.getAddress());
        userService.persist(user);
    }

    @Test
    public void addTripCreatesAttendlistAndAddsTripCreatorAutomatically() {
        //Check that attendList table is empty when trip is not linked with owner
        final List<Attendlist> attendlistsBeforePersist = em.getEntityManager().createQuery("SELECT a FROM Attendlist  a", Attendlist.class).getResultList();
        assertEquals(0, attendlistsBeforePersist.size());


        //Trip and attendList are persisted into the table and retrieving the data from attendList table should return 1 result
        userService.addTrip(user, trip);
        final List<Attendlist> attendlistsAfterPersist = em.getEntityManager().createQuery("SELECT a FROM Attendlist  a", Attendlist.class).getResultList();
        assertEquals(1, attendlistsAfterPersist.size());
        verify(tripDao).persist(trip);
        verify(attendlistDao).persist(user.getAttendlists().get(0));
        //Check that after attendlist persist the creator was added automatically into the table
        assertEquals(user.getId(), attendlistsAfterPersist.get(0).getAdmin().getId());

        //Trip and attendList are persisted into the table and retrieving the data from attendList table should return 2 results
        trip = Generator.generateTrip(user);
        em.persist(trip);
        userService.addTrip(user, trip);
        final List<Attendlist> attendlistsAfterPersist2 = em.getEntityManager().createQuery("SELECT a FROM Attendlist  a", Attendlist.class).getResultList();
        assertEquals(2, attendlistsAfterPersist2.size());
        verify(tripDao).persist(trip);
        verify(attendlistDao).persist(user.getAttendlists().get(1));
        assertEquals(user.getId(), attendlistsAfterPersist2.get(1).getAdmin().getId());

        //Try to create another user which creates the trip with different id
        user = Generator.generateUser();
        trip = Generator.generateTrip(user);
        em.persist(user.getAddress());
        em.persist(user);
        em.persist(trip);
        addressDao.persist(user.getAddress());
        userService.persist(user);
        userService.addTrip(user, trip);
        final List<Attendlist> attendlistsAfterPersist3 = em.getEntityManager().createQuery("SELECT a FROM Attendlist  a", Attendlist.class).getResultList();
        assertEquals(3, attendlistsAfterPersist3.size());
        verify(tripDao).persist(trip);
        verify(attendlistDao).persist(user.getAttendlists().get(0));
        //Check that after attendlist persist the creator was added automatically into the table
        assertEquals(user.getId(), attendlistsAfterPersist3.get(2).getAdmin().getId());
    }

    @Test
    public void removeTripRemovesAllUsersInAttendlist() {
        List<User> buffer = Generator.generateUsers(2, em, addressDao, userService);

        userService.addTrip(user, trip);

        for (User us : buffer) {
            JoinRequest joinRequest = new JoinRequest();
            joinRequest.setRequester(us);
            joinRequest.setTrip(trip);
            em.persist(joinRequest);
            userService.addJoinRequest(us, joinRequest);
            userService.approveJoinRequest(joinRequest);
            em.persist(us.getAttendlists().get(0));
        }
        final List<Attendlist> attends = em.getEntityManager().createQuery("SELECT a FROM Attendlist  a", Attendlist.class).getResultList();
        final List<User> users = em.getEntityManager().createQuery("SELECT a FROM User  a", User.class).getResultList();
        final List<JoinRequest> joinRequests = em.getEntityManager().createQuery("SELECT a FROM JoinRequest  a", JoinRequest.class).getResultList();
        assertEquals(1, user.getTrips().size());
        assertEquals(1, user.getAttendlists().size());
        for (User us : buffer) {
            assertEquals(1, us.getAttendlists().size());
        }

        userService.removeTrip(user, trip);

        assertEquals(0, user.getAttendlists().size());
        assertEquals(0, user.getTrips().size());
        for (User us : buffer) {
            assertEquals(0, us.getAttendlists().size());
        }
    }

    @Test
    public void approveRequestAddsUserIntoAttendlist() {
        List<User> buffer = Generator.generateUsers(2, em, addressDao, userService);

        userService.addTrip(user, trip);

        final List<Attendlist> attends = em.getEntityManager().createQuery("SELECT a FROM Attendlist  a", Attendlist.class).getResultList();
        assertEquals(1, attends.size());
        verify(userDao).update(user);

        for (User us : buffer) {
            JoinRequest joinRequest = new JoinRequest();
            joinRequest.setRequester(us);
            joinRequest.setTrip(trip);
            em.persist(joinRequest);

            userService.addJoinRequest(us, joinRequest);
            verify(joinRequestDao).persist(joinRequest);
            verify(userDao).update(us);

            userService.approveJoinRequest(joinRequest);
            em.persist(us.getAttendlists().get(0));
            verify(attendlistDao).persist(us.getAttendlists().get(0));
            verify(joinRequestDao).update(joinRequest);
            verify(userDao, times(2)).update(joinRequest.getRequester());
        }

        final List<Attendlist> attendsAfterInserting = em.getEntityManager().createQuery("SELECT a FROM Attendlist  a", Attendlist.class).getResultList();
        assertEquals(3, attendsAfterInserting.size());
    }

    @Test
    public void addJoinRequestForRegularUser() {
        List<User> buffer = Generator.generateUsers(1, em, addressDao, userService);

        userService.addTrip(user, trip);
        verify(userDao).update(user);

        for (User us : buffer) {
            JoinRequest joinRequest = new JoinRequest();
            joinRequest.setRequester(us);
            joinRequest.setTrip(trip);
            em.persist(joinRequest);

            userService.addJoinRequest(us, joinRequest);
            verify(joinRequestDao).persist(joinRequest);
            verify(userDao).update(us);

            //Try to produce the second joinRequest
            Exception exception = assertThrows(JoinRequestException.class, () -> {
                userService.addJoinRequest(us, joinRequest);
            });
            assertEquals("User cannot create more than one join request to one trip.", exception.getMessage());
        }
    }

    @Test
    public void addJoinRequestForRejectedRegularUser() {
        List<User> buffer = Generator.generateUsers(1, em, addressDao, userService);
        User us = buffer.get(0);

        userService.addTrip(user, trip);
        verify(userDao).update(user);

        JoinRequest joinRequest = new JoinRequest();
        joinRequest.setRequester(us);
        joinRequest.setTrip(trip);
        joinRequest.setStatus(RequestStatus.REJECTED);
        em.persist(joinRequest);
        assertEquals(RequestStatus.REJECTED, joinRequest.getStatus());

        userService.addJoinRequest(us, joinRequest);
        assertEquals(RequestStatus.IN_PROGRESS, joinRequest.getStatus());
        verify(joinRequestDao).persist(joinRequest);
        verify(userDao).update(us);
    }

    @Test
    public void addJoinRequestForAdminWithException() {
        JoinRequest joinRequest = new JoinRequest();
        joinRequest.setRequester(user);
        joinRequest.setTrip(trip);
        em.persist(joinRequest);

        //Try to produce join request for admin
        Exception exception = assertThrows(JoinRequestException.class, () -> {
            userService.addJoinRequest(user, joinRequest);
        });
        assertEquals("User cannot create join request to the trip he is author of.", exception.getMessage());
    }

    @Test
    public void approveJoinRequestWithMandatoryFieldsMissing() {
        List<User> buffer = Generator.generateUsers(2, em, addressDao, userService);

        userService.addTrip(user, trip);
        verify(userDao).update(user);

        for (User us : buffer) {
            JoinRequest joinRequest = new JoinRequest();
            joinRequest.setRequester(us);

            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.addJoinRequest(us, joinRequest);
            });
            assertEquals("Fields in joinRequest can't be null!", exception.getMessage());
        }
    }
}
