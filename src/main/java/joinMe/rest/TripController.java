package joinMe.rest;

import joinMe.db.entity.*;
import joinMe.db.exception.NotFoundException;
import joinMe.db.exception.ValidationException;
import joinMe.rest.dto.CommentDTO;
import joinMe.rest.dto.Mapper;
import joinMe.rest.dto.TripDTO;
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

@RestController
@RequestMapping("/trips")
public class TripController {

    private static final Logger LOG = LoggerFactory.getLogger(TripController.class);

    private final TripService tripService;

    private final UserService userService;

    private final AttendlistService attendlistService;

    private final CommentService commentService;

    private final Mapper mapper;

    @Autowired
    public TripController(TripService tripService, UserService userService, AttendlistService attendlistService, CommentService commentService, Mapper mapper) {
        this.tripService = tripService;
        this.userService = userService;
        this.attendlistService = attendlistService;
        this.commentService = commentService;
        this.mapper = mapper;
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_GUEST')")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateTrip(@PathVariable Integer id, @RequestBody TripDTO tripDTO) {
        final Trip trip = tripService.findByID(id);
        if (!trip.getId().equals(tripDTO.getId())) {
            throw new ValidationException("Trip identifier in the data does not match the one in the request URL.");
        }

        Trip tripToUpdate = mapper.toEntity(tripDTO);
        tripService.update(trip, tripToUpdate);
        LOG.debug("Updated trip {}.", tripDTO);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createTrip(Authentication auth, @RequestBody TripDTO tripDTO) {
        assert auth.getPrincipal() instanceof UserDetails;
        User user = ((UserDetails) auth.getPrincipal()).getUser();

        Trip trip = mapper.toEntity(tripDTO);

        userService.addTrip(user, trip);
        Attendlist attendlist = attendlistService.create(user, trip);

        LOG.debug("Created trip {}.", trip);
        LOG.debug("Created attend list {}.", attendlist);
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/{id}", trip.getId());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_GUEST')")
    @DeleteMapping(value = "/{id}")
    public void deleteTrip(Authentication auth, @PathVariable int id) {
        assert auth.getPrincipal() instanceof UserDetails;
        final User user = ((UserDetails) auth.getPrincipal()).getUser();

        try {
            Trip trip = getTrip(id);

            if (user.getRole() != Role.ADMIN && !trip.getAuthor().getId().equals(user.getId())) {
                throw new AccessDeniedException("Cannot delete trip of another user.");
            }
            userService.removeTrip(user, trip);
            tripService.remove(trip);
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_GUEST')")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TripDTO> getAllActiveTrips() {
        LOG.info("Retrieving all active trips.");
        return tripService.findAllActiveTrips()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @GetMapping(value = "/current", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TripDTO> getCurrentUserTrips(Authentication auth) {
        assert auth.getPrincipal() instanceof UserDetails;
        final User user = ((UserDetails) auth.getPrincipal()).getUser();
        return user.getTrips()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public TripDTO getTrip(Authentication auth, @PathVariable int id) {
        assert auth.getPrincipal() instanceof UserDetails;
        Trip trip = getTrip(id);
        return mapper.toDto(trip);
    }

    @PostMapping(value = "/{tripID}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> addComment(Authentication auth, @PathVariable int tripID, @RequestBody CommentDTO commentDTO) {
        assert auth.getPrincipal() instanceof UserDetails;
        Trip trip = getTrip(tripID);
        Comment comment = mapper.toEntity(commentDTO);

        tripService.addComment(trip, comment);
        commentService.persist(comment);
        LOG.debug("Added comment {}.", comment);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    private Trip getTrip(int id) {
        Trip trip = tripService.findByID(id);
        if (trip == null) {
            throw NotFoundException.create("Trip", id);
        }

        return trip;
    }
}
