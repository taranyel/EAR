package joinMe.service;

import joinMe.db.dao.AttendlistDao;
import joinMe.db.dao.JoinRequestDao;
import joinMe.db.dao.TripDao;
import joinMe.db.dao.UserDao;
import joinMe.db.entity.*;
import joinMe.db.exception.JoinRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class UserService {

    private final UserDao userDao;

    private final JoinRequestDao joinRequestDao;

    private final AttendlistDao attendlistDao;

    private final PasswordEncoder passwordEncoder;

    private final TripDao tripDao;

    @Autowired
    public UserService(UserDao userDao, JoinRequestDao joinRequestDao, AttendlistDao attendlistDao, PasswordEncoder passwordEncoder, TripDao tripDao) {
        this.userDao = userDao;
        this.joinRequestDao = joinRequestDao;
        this.attendlistDao = attendlistDao;
        this.passwordEncoder = passwordEncoder;
        this.tripDao = tripDao;
    }

    @Transactional
    public void persist(User user) {
        Objects.requireNonNull(user);
        user.encodePassword(passwordEncoder);
        userDao.persist(user);
    }

    /// When trip is created, attendlist (chat) is created automatically and trip creator is added to the chat as admin
    @Transactional
    public void addTrip(User user, Trip trip) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(trip);
        user.addTrip(trip);

        Attendlist attendlist = new Attendlist(user, trip);
        user.addAttendlist(attendlist);
        tripDao.persist(trip);
        attendlistDao.persist(attendlist);
        userDao.update(user);
    }

    /// When user deletes trip, chat is also deleted for every trip joiner
    @Transactional
    public void removeTrip(User user, Trip trip) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(trip);

        List<User> joiners = userDao.getAllJoinersOfAttendlist(trip);
        for (User joiner : joiners) {
            Attendlist attendlist = attendlistDao.findByTripAndJoiner(trip, joiner);
            joiner.removeAttendlist(attendlist);
            userDao.update(joiner);
        }

        user.removeTrip(trip);
        userDao.update(user);
    }

    @Transactional
    public void addWishlist(User user, Wishlist wishlist) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(wishlist);
        user.addWishlist(wishlist);
        userDao.update(user);
    }

    @Transactional
    public void removeWishlist(User user, Wishlist wishlist) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(wishlist);
        user.removeWishlist(wishlist);
        userDao.update(user);
    }

    @Transactional
    public void addComplaint(User user, Complaint complaint) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(complaint);
        user.addComplaint(complaint);
        userDao.update(user);
    }

    @Transactional
    public void removeComplaint(User user, Complaint complaint) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(complaint);
        user.removeComplaint(complaint);
        userDao.update(user);
    }

    @Transactional
    public void addAttendlist(User user, Attendlist attendlist) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(attendlist);

        User tripCreator = attendlist.getAdmin();

        if (user == tripCreator) {
            throw new JoinRequestException("Creator added to the trip automatically.");
        }

        JoinRequest existingRequest = joinRequestDao.findByRequesterAndTrip(attendlist.getJoiner(), attendlist.getTrip());
        if (existingRequest == null) {
            throw new JoinRequestException("Joiner cannot be added to attendlist without join request.");
        }

        if (existingRequest.getStatus() != RequestStatus.APPROVED) {
            throw new JoinRequestException("Joiner cannot be added to attendlist without approval.");
        }

        user.addAttendlist(attendlist);
        attendlistDao.update(attendlist);
        userDao.update(user);
    }

    @Transactional
    public void removeAttendlist(User user, Attendlist attendlist) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(attendlist);
        user.removeAttendlist(attendlist);
        userDao.update(user);
    }

    @Transactional
    public void addJoinRequest(User user, JoinRequest joinRequest) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(joinRequest);

        if (joinRequest.getRequester() == null || joinRequest.getTrip() == null) {
            throw new IllegalArgumentException("Fields in joinRequest can't be null!");
        }

        User tripCreator = joinRequest.getTrip().getAuthor();
        if (user == tripCreator) {
            throw new JoinRequestException("User cannot create join request to the trip he is author of.");
        }

        JoinRequest existingRequest = joinRequestDao.findByRequesterAndTrip(joinRequest.getRequester(), joinRequest.getTrip());
        if (existingRequest.getStatus() != RequestStatus.CREATED && existingRequest.getStatus() != RequestStatus.REJECTED) {
            throw new JoinRequestException("User cannot create more than one join request to one trip.");
        }

        joinRequest.setStatus(RequestStatus.IN_PROGRESS);
        user.addJoinRequest(joinRequest);
        joinRequestDao.persist(joinRequest);
        userDao.update(user);
    }

    @Transactional
    public void removeJoinRequest(User user, JoinRequest joinRequest) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(joinRequest);
        user.removeJoinRequest(joinRequest);
        userDao.update(user);
    }

    @Transactional
    public User findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Transactional
    public List<User> getAllJoinersOfAttendlist(Trip trip) {
        return userDao.getAllJoinersOfAttendlist(trip);
    }

    public List<Attendlist> getAllAttendlists(User user) {
        return user.getAttendlists();
    }

    @Transactional
    public void approveJoinRequest(JoinRequest joinRequest) {
        Objects.requireNonNull(joinRequest);
        User requester = joinRequest.getRequester();

        Attendlist attendlist = new Attendlist(requester, joinRequest.getTrip());
        requester.addAttendlist(attendlist);
        joinRequest.setStatus(RequestStatus.APPROVED);

        attendlistDao.persist(attendlist);
        joinRequestDao.update(joinRequest);
        userDao.update(requester);
    }

    @Transactional
    public void rejectJoinRequest(JoinRequest joinRequest) {
        Objects.requireNonNull(joinRequest);
        joinRequest.setStatus(RequestStatus.REJECTED);
        joinRequestDao.update(joinRequest);
    }

    @Transactional
    public void blockUser(User user) {
        user.setStatus(AccountStatus.BLOCKED);
        userDao.update(user);
    }

    @Transactional
    public void unblockUser(User user) {
        user.setStatus(AccountStatus.ACTIVE);
        userDao.update(user);
    }
}
