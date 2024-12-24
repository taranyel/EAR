package joinMe.service;

import joinMe.db.dao.AttendlistDao;
import joinMe.db.dao.JoinRequestDao;
import joinMe.db.dao.TripDao;
import joinMe.db.dao.UserDao;
import joinMe.db.entity.*;
import joinMe.db.exception.JoinRequestException;
import joinMe.security.SecurityUtils;
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
    public void addJoinRequest(JoinRequest joinRequest) {
        Objects.requireNonNull(joinRequest);
        User requester = joinRequest.getRequester();
        requester.addJoinRequest(joinRequest);
        userDao.update(requester);
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

    public List<Trip> getCurrentUserTrips() {
        final User currentUser = SecurityUtils.getCurrentUser();
        assert currentUser != null;
        return currentUser.getTrips();
    }

    public List<Attendlist> getCurrentUserAttendLists() {
        final User currentUser = SecurityUtils.getCurrentUser();
        assert currentUser != null;
        return currentUser.getAttendlists();
    }

    public List<Wishlist> getCurrentUserWishlists() {
        final User currentUser = SecurityUtils.getCurrentUser();
        assert currentUser != null;
        return currentUser.getWishlists();
    }

    public List<JoinRequest> getCurrentUserJoinRequests() {
        final User currentUser = SecurityUtils.getCurrentUser();
        assert currentUser != null;
        return currentUser.getJoinRequests();
    }

    public List<JoinRequest> getCurrentUserJoinRequestsForApproval() {
        final User currentUser = SecurityUtils.getCurrentUser();
        assert currentUser != null;
        return joinRequestDao.getJoinRequestsForApproval(currentUser);
    }

    public List<Complaint> getComplaintsToCurrentUser() {
        final User currentUser = SecurityUtils.getCurrentUser();
        assert currentUser != null;
        return currentUser.getComplaints();
    }
}
