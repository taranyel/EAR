package joinMe.rest;

import joinMe.db.entity.*;
import joinMe.db.exception.NotFoundException;
import joinMe.rest.util.RestUtils;
import joinMe.security.model.UserDetails;
import joinMe.service.AttendlistService;
import joinMe.service.CommentService;
import joinMe.service.TripService;
import joinMe.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/trips")
public class TripController {

    private static final Logger LOG = LoggerFactory.getLogger(TripController.class);

    private final TripService tripService;

    private final UserService userService;

    private final AttendlistService attendlistService;

    private final CommentService commentService;

    @Autowired
    public TripController(TripService tripService, UserService userService, AttendlistService attendlistService, CommentService commentService) {
        this.tripService = tripService;
        this.userService = userService;
        this.attendlistService = attendlistService;
        this.commentService = commentService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createTrip(Authentication auth, @RequestBody Trip trip) {
        assert auth.getPrincipal() instanceof UserDetails;
        User user = ((UserDetails) auth.getPrincipal()).getUser();
        userService.addTrip(user, trip);
        tripService.persist(trip);
        Attendlist attendlist = attendlistService.create(user, trip);

        LOG.debug("Created trip {}.", trip);
        LOG.debug("Created attend list {}.", attendlist);
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/{id}", trip.getId());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_GUEST')")
    @DeleteMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void deleteTrip(Authentication auth, @PathVariable int id) {
        Trip trip = tripService.findByID(id);
        if (trip == null) {
            throw NotFoundException.create("Trip", id);
        }
        assert auth.getPrincipal() instanceof UserDetails;
        final User user = ((UserDetails) auth.getPrincipal()).getUser();

        if (user.getRole() != Role.ADMIN && !trip.getAuthor().getId().equals(user.getId())) {
            throw new AccessDeniedException("Cannot delete trip of another user.");
        }
        userService.removeTrip(user, trip);
        tripService.remove(trip);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_GUEST')")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Trip> getAllActiveTrips(Authentication auth) {
        assert auth.getPrincipal() instanceof UserDetails;
        LOG.info("Retrieving all active trips.");
        return tripService.findAllActiveTrips();
    }

    @GetMapping(value = "/current", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Trip> getCurrentUserTrips(Authentication auth) {
        assert auth.getPrincipal() instanceof UserDetails;
        final User user = ((UserDetails) auth.getPrincipal()).getUser();
        return user.getTrips();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Trip getTrip(Authentication auth, @PathVariable int id) {
        assert auth.getPrincipal() instanceof UserDetails;
        Trip trip = tripService.findByID(id);
        if (trip == null) {
            throw NotFoundException.create("Trip", id);
        }
        return trip;
    }

    @PostMapping(value = "/{tripID}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> addComment(Authentication auth, @PathVariable int tripID, @RequestBody Comment comment) {
        assert auth.getPrincipal() instanceof UserDetails;
        Trip trip = getTrip(auth, tripID);

        tripService.addComment(trip, comment);
        commentService.persist(comment);
        LOG.debug("Added comment {}.", comment);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
