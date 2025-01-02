package joinMe.service;

import joinMe.db.dao.*;
import joinMe.db.entity.*;
import joinMe.db.exception.JoinRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Objects;

@Service
public class UserService {

    private final UserDao userDao;

    private final TripDao tripDao;

    private final JoinRequestDao joinRequestDao;

    private final AttendlistDao attendlistDao;

    private final PasswordEncoder passwordEncoder;

    private final ComplaintDao complaintDao;

    private final WishlistDao wishlistDao;


    @Autowired
    public UserService(UserDao userDao, JoinRequestDao joinRequestDao, AttendlistDao attendlistDao,
                       ComplaintDao complaintDao, WishlistDao wishlistDao, PasswordEncoder passwordEncoder, TripDao tripDao) {
        this.userDao = userDao;
        this.joinRequestDao = joinRequestDao;
        this.attendlistDao = attendlistDao;
        this.complaintDao = complaintDao;
        this.wishlistDao = wishlistDao;
        this.passwordEncoder = passwordEncoder;
        this.tripDao = tripDao;
    }

    @Transactional
    public void update(User current, User newUser) throws AccessDeniedException {
        Objects.requireNonNull(current);
        Objects.requireNonNull(newUser);

        if (userDao.findByEmail(newUser.getEmail()) != null && !Objects.equals(current.getEmail(), newUser.getEmail())) {
            throw new AccessDeniedException("User with email already exists.");
        }

        if (userDao.findByUsername(newUser.getUsername()) != null && !Objects.equals(current.getUsername(), newUser.getUsername())) {
            throw new AccessDeniedException("User with username already exists.");
        }

        current.setFirstName(newUser.getFirstName());
        current.setLastName(newUser.getLastName());
        current.setEmail(newUser.getEmail());
        current.setPassword(passwordEncoder.encode(newUser.getPassword()));
        current.setUsername(newUser.getUsername());
        current.setImagePath(newUser.getImagePath());
        current.setBirthdate(newUser.getBirthdate());
        userDao.update(current);
    }

    @Transactional
    public void remove(User user) {
        Objects.requireNonNull(user);
        userDao.remove(user);
    }

    @Transactional
    public void persist(User user) {
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
    public User findByID(Integer id) {
        return userDao.find(id);
    }

    /// When trip is created, attendlist (chat) is created automatically and trip creator is added to the chat as admin
    @Transactional
    public void addTrip(User user, Trip trip) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(trip);
        trip.setAuthor(user);
        tripDao.persist(trip);
        user.addTrip(trip);
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
    public List<User> getAllJoinersOfAttendlistByID(Integer id) {
        return userDao.getAllJoinersOfAttendlistByID(id);
    }

    @Transactional
    public void approveJoinRequest(JoinRequest joinRequest) {
        Objects.requireNonNull(joinRequest);
        User requester = joinRequest.getRequester();

        Attendlist attendlist = Attendlist.builder()
                .joiner(requester)
                .trip(joinRequest.getTrip())
                .build();
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
