package joinMe.service;

import joinMe.db.dao.AttendlistDao;
import joinMe.db.dao.UserDao;
import joinMe.db.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class UserService {

    private final UserDao userDao;

    private final AttendlistDao attendlistDao;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserDao userDao, AttendlistDao attendlistDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.attendlistDao = attendlistDao;
        this.passwordEncoder = passwordEncoder;
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
    public void addComment(User user, Comment comment) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(comment);
        user.addComment(comment);
        userDao.update(user);
    }

    @Transactional
    public void removeComment(User user, Comment comment) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(comment);
        user.removeComment(comment);
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
}
