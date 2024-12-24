package joinMe.rest;

import joinMe.db.entity.Role;
import joinMe.db.entity.Trip;
import joinMe.db.entity.User;
import joinMe.db.exception.NotFoundException;
import joinMe.rest.util.RestUtils;
import joinMe.security.model.UserDetails;
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

@RestController
@RequestMapping("/trips")
public class TripController {

    private static final Logger LOG = LoggerFactory.getLogger(TripController.class);

    private final TripService tripService;

    private final UserService userService;

    @Autowired
    public TripController(TripService tripService, UserService userService) {
        this.tripService = tripService;
        this.userService = userService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createTrip(@RequestBody Trip trip) {
        tripService.persist(trip);
        LOG.debug("Created trip {}.", trip);
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
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Trip> getAllActiveTrips() {
        return tripService.findAllActiveTrips();
    }

    @GetMapping(value = "/current", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Trip> getCurrentUserTrips() {
        return userService.getCurrentUserTrips();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Trip getTrip(@PathVariable int id) {
        Trip trip = tripService.findByID(id);
        if (trip == null) {
            throw NotFoundException.create("Trip", id);
        }
        return trip;
    }
}
