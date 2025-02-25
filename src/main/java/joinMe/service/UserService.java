package joinMe.service;

import joinMe.db.dao.AttendlistDao;
import joinMe.db.dao.JoinRequestDao;
import joinMe.db.dao.UserDao;
import joinMe.db.dao.WishlistDao;
import joinMe.db.entity.*;
import joinMe.db.exception.NotFoundException;
import joinMe.security.model.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class UserService {

    private final UserDao userDao;

    private final JoinRequestDao joinRequestDao;

    private final AttendlistDao attendlistDao;

    private final PasswordEncoder passwordEncoder;

    private final WishlistDao wishlistDao;

    @Autowired
    public UserService(UserDao userDao, JoinRequestDao joinRequestDao, AttendlistDao attendlistDao, WishlistDao wishlistDao,
                       PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.joinRequestDao = joinRequestDao;
        this.attendlistDao = attendlistDao;
        this.wishlistDao = wishlistDao;
        this.passwordEncoder = passwordEncoder;
    }

    public void update(User user) {
        Objects.requireNonNull(user);
        userDao.update(user);
    }

    public void update(User current, User newUser) {
        Objects.requireNonNull(current);
        Objects.requireNonNull(newUser);

        if (userDao.findByEmail(newUser.getEmail()) != null && !Objects.equals(current.getEmail(), newUser.getEmail())) {
            throw new IllegalArgumentException(
                    String.format("User with \"%s\" email already exists", current.getEmail())
            );
        }

        if (userDao.findByUsername(newUser.getUsername()) != null && !Objects.equals(current.getUsername(), newUser.getUsername())) {
            throw new IllegalArgumentException(
                    String.format("User with \"%s\" username already exists", current.getUsername())
            );
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

    public void remove(User user) {
        Objects.requireNonNull(user);
        userDao.remove(user);
    }

    public void persist(User user) {
        Objects.requireNonNull(user);

        if (userDao.findByEmail(user.getEmail()) != null) {
            throw new IllegalArgumentException(
                    String.format("User with \"%s\" email already exists.", user.getEmail())
            );
        }
        if (userDao.findByUsername(user.getUsername()) != null) {
            throw new IllegalArgumentException(
                    String.format("User with \"%s\" username already exists.", user.getUsername())
            );
        }

        user.encodePassword(passwordEncoder);
        userDao.persist(user);
    }

    public User findByID(Integer id) {
        User user = userDao.find(id);
        if (user == null) {
            throw NotFoundException.create("User", id);
        }
        return user;
    }

    /// When trip is created, attendlist (chat) is created automatically and trip creator is added to the chat as admin
    public void addTrip(User user, Trip trip) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(trip);
        user.addTrip(trip);
        userDao.update(user);
    }

    public void addRating(User user, Rating rating) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(rating);
        rating.setOwner(user);
        user.addRating(rating);
        userDao.update(user);
    }

    /// When user deletes trip, chat is also deleted for every trip joiner
    public void removeTrip(User user, Trip trip) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(trip);
        user.removeAttendlist(attendlistDao.findByTripAndJoiner(trip, user));
        user.removeTrip(trip);
        userDao.update(user);
    }

    public void addWishlist(User user, Wishlist wishlist) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(wishlist);
        user.addWishlist(wishlist);
        userDao.update(user);
    }

    public void removeWishlist(User user, Wishlist wishlist) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(wishlist);
        user.removeWishlist(wishlist);
        userDao.update(user);
        wishlistDao.remove(wishlist);
    }

    public void addComplaint(User user, Complaint complaint) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(complaint);
        user.addComplaint(complaint);
        userDao.update(user);
    }

    public void removeComplaint(User user, Complaint complaint) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(complaint);
        user.removeComplaint(complaint);
        userDao.update(user);
    }

    public void leaveAttendlist(User user, Attendlist attendlist) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(attendlist);
        user.removeAttendlist(attendlist);
        userDao.update(user);
    }

    public void addJoinRequest(JoinRequest joinRequest) {
        Objects.requireNonNull(joinRequest);
        User requester = joinRequest.getRequester();
        requester.addJoinRequest(joinRequest);
        userDao.update(requester);
    }

    public void cancelJoinRequest(User user, JoinRequest joinRequest) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(joinRequest);
        user.removeJoinRequest(joinRequest);
        userDao.update(user);
    }

    public Long getId(String username) {
        return Long.valueOf(userDao.findByUsername(username).getId());
    }

    public boolean exists(String email) {
        return userDao.findByUsername(email) != null;
    }

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

    public void rejectJoinRequest(JoinRequest joinRequest) {
        Objects.requireNonNull(joinRequest);
        joinRequest.setStatus(RequestStatus.REJECTED);
        joinRequestDao.update(joinRequest);
    }

    public void blockUser(User user) {
        user.setStatus(AccountStatus.BLOCKED);
        userDao.update(user);
    }

    public void unblockUser(User user) {
        user.setStatus(AccountStatus.ACTIVE);
        userDao.update(user);
    }

    public boolean isAdmin(User user) {
        return user.getRole().getName().equals("ROLE_ADMIN");
    }

    public List<User> getAllJoinersOfTrip(Trip trip) {
        Objects.requireNonNull(trip);
        return userDao.getAllJoinersOfAttendlistByTrip(trip);
    }

    public void setAdmin(User user) {
        user.setRole(Role.ADMIN);
        userDao.update(user);
    }

    public User getCurrent(Authentication auth) {
        assert auth.getPrincipal() instanceof UserDetails;
        final int userId = ((UserDetails) auth.getPrincipal()).getUser().getId();
        return findByID(userId);
    }

    public static void isBlocked(User user) throws AccessDeniedException {
        if (user.getStatus() == AccountStatus.BLOCKED) {
            throw new AccessDeniedException("Your account is blocked.");
        }
    }
}
