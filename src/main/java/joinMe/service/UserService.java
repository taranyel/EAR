package joinMe.service;

import joinMe.db.dao.*;
import joinMe.db.entity.*;
import joinMe.security.model.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;

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

        User user;
        if (isAdmin(current)) {
            user = findByID(newUser.getId());
        } else {
            user = current;
        }
        if(newUser.getRole() == null) {
            newUser.setRole(user.getRole());
        }

        if (userDao.findByEmail(newUser.getEmail()) != null && !Objects.equals(user.getEmail(), newUser.getEmail())) {
            throw new IllegalArgumentException(
                    String.format("User with \"%s\" email already exists", user.getEmail())
            );
        }

        if (userDao.findByUsername(newUser.getUsername()) != null && !Objects.equals(user.getUsername(), newUser.getUsername())) {
            throw new IllegalArgumentException(
                    String.format("User with \"%s\" username already exists", user.getUsername())
            );
        }

        user.setFirstName(newUser.getFirstName());
        user.setLastName(newUser.getLastName());
        user.setEmail(newUser.getEmail());
        user.setPassword(passwordEncoder.encode(newUser.getPassword()));
        user.setUsername(newUser.getUsername());
        user.setImagePath(newUser.getImagePath());
        user.setBirthdate(newUser.getBirthdate());
        user.setRole(newUser.getRole());
        userDao.update(user);
    }

    public void remove(User user) {
        Objects.requireNonNull(user);
        userDao.remove(user);
    }

    public void persist(User user) {
        Objects.requireNonNull(user);

        if (userDao.findByEmail(user.getEmail()) != null && userDao.findByUsername(user.getUsername()) != null) {
            throw new IllegalArgumentException(
                    String.format("User with \"%s\" email and \"%s\" username already exists", user.getEmail(), user.getUsername())
            );
        }
        if (userDao.findByEmail(user.getEmail()) != null) {
            throw new IllegalArgumentException(
                    String.format("User with \"%s\" email already exists", user.getEmail())
            );
        }
        if (userDao.findByUsername(user.getUsername()) != null) {
            throw new IllegalArgumentException(
                    String.format("User with \"%s\" username already exists", user.getUsername())
            );
        }

        user.encodePassword(passwordEncoder);
        userDao.persist(user);
    }

    public User findByID(Integer id) {
        return userDao.find(id);
    }

    /// When trip is created, attendlist (chat) is created automatically and trip creator is added to the chat as admin
    public void addTrip(User user, Trip trip) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(trip);
        user.addTrip(trip);
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

    public ResponseEntity<String> checkAuthRole(Authentication auth, User user, String methodType, Logger LOG) {
        if (auth == null || !auth.isAuthenticated()) {
            // User is not logged in
            if (!user.getRole().getName().equals("ROLE_USER")) {
                String message = String.format("Unauthorized %s attempt for non-USER role by an anonymous user.", methodType);
                LOG.warn(message);
                return new ResponseEntity<>(message, HttpStatus.UNAUTHORIZED);
            }
        } else {
            // User is logged in
            boolean isAdmin = auth.getAuthorities().toString().equals("[ROLE_ADMIN]");

            if (!isAdmin && user.getRole().getName().equals("ROLE_ADMIN")) {
                String message = String.format("Unauthorized %s attempt for ADMIN role by a USER.", methodType);
//                String message = "Unauthorized registration attempt for ADMIN role by a USER.";
                LOG.warn(message);
                return new ResponseEntity<>(message, HttpStatus.FORBIDDEN);
            }
        }
        return null;
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
}
