package joinMe.service;

import joinMe.db.dao.*;
import joinMe.db.entity.*;
import joinMe.db.exception.JoinRequestException;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class UserService {

    private final UserDao userDao;

    private final TripDao tripdao;

    private final JoinRequestDao joinRequestDao;

    private final AttendlistDao attendlistDao;

    private final PasswordEncoder passwordEncoder;

    private final ComplaintDao complaintDao;

    private final WishlistDao wishlistDao;


    @Autowired
    public UserService(UserDao userDao, TripDao tripDao, JoinRequestDao joinRequestDao, AttendlistDao attendlistDao,
                       ComplaintDao complaintDao, WishlistDao wishlistDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.tripdao = tripDao;
        this.joinRequestDao = joinRequestDao;
        this.attendlistDao = attendlistDao;
        this.complaintDao = complaintDao;
        this.wishlistDao = wishlistDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void persist(User user) {
        if (userDao.findByUsername(user.getUsername()) != null && userDao.findByEmail(user.getEmail()) != null) {
            throw new IllegalArgumentException("User with this username and email already exists");
        }
        if (userDao.findByEmail(user.getEmail()) != null) {
            throw new IllegalArgumentException("User with this email already exists");
        }
        if (userDao.findByUsername(user.getUsername()) != null) {
            throw new IllegalArgumentException("User with this username already exists");
        }
        Objects.requireNonNull(user);
        user.encodePassword(passwordEncoder);
        userDao.persist(user);
    }

    @Transactional
    public void update(Long id, String newFirstName, String newLastName, String newPassword) {
        User user = userDao.find(Math.toIntExact(id));
        if (newPassword.isEmpty() || newFirstName.isEmpty() || newLastName.isEmpty()) {
            throw new IllegalArgumentException("Credentials can't be empty.");
        }
        user.setFirstName(newFirstName);
        user.setLastName(newLastName);
        user.setPassword(passwordEncoder.encode(newPassword));
        userDao.update(user);
    }

    @Transactional
    public boolean delete(Long id) {
        if (!userDao.exists(Math.toIntExact(id))) {
            return false;
        }
        userDao.remove(userDao.find(Math.toIntExact(id)));
        return true;
    }

    /// When trip is created, attendlist (chat) is created automatically and trip creator is added to the chat as admin
    @Transactional
    public void addTrip(User user, Trip trip) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(trip);
        user.setPassword(userDao.find(user.getId()).getPassword());
        user.setAttendlists(userDao.find(user.getId()).getAttendlists());
        trip.setAuthor(user);
        user.addTrip(trip);
//        user.getTrips().add(trip);
        tripdao.persist(trip);
        userDao.update(user);
    }

    /// When user deletes trip, chat is also deleted for every trip joiner
    @Transactional
    public void removeTrip(User user, Trip trip) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(trip);
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
        wishlistDao.remove(wishlist);
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
        complaintDao.remove(complaint);
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
    public void leaveAttendlist(User user, Attendlist attendlist) {
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
    public void cancelJoinRequest(User user, JoinRequest joinRequest) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(joinRequest);
        user.removeJoinRequest(joinRequest);
        userDao.update(user);
        joinRequestDao.remove(joinRequest);
    }

    @Transactional
    public Long getId(String username) {
        return Long.valueOf(userDao.findByUsername(username).getId());
    }

    @Transactional
    public boolean exists(String email) {
        return userDao.findByUsername(email) != null;
    }

    @Transactional
    public boolean existsById(Long id) {
        return userDao.find(Math.toIntExact(id)) != null;
    }

    @Transactional
    public List<User> getAllJoinersOfAttendlistByID(Integer id) {
        return userDao.getAllJoinersOfAttendlistByID(id);
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
