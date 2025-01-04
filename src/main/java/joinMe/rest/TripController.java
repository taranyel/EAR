package joinMe.rest;

import joinMe.db.entity.*;
import joinMe.db.exception.NotFoundException;
import joinMe.rest.dto.CommentDTO;
import joinMe.rest.dto.Mapper;
import joinMe.rest.dto.TripDTO;
import joinMe.rest.util.RestUtils;
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

import java.time.LocalDate;
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

    private final Mapper mapper;

    @Autowired
    public TripController(TripService tripService, UserService userService, AttendlistService attendlistService, CommentService commentService, Mapper mapper) {
        this.tripService = tripService;
        this.userService = userService;
        this.attendlistService = attendlistService;
        this.commentService = commentService;
        this.mapper = mapper;
    }

    @PreAuthorize("!anonymous")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<String> updateTrip(Authentication auth, @PathVariable Integer id, @RequestBody TripDTO tripDTO) {
        if (tripDTO == null) {
            return new ResponseEntity<>("Data is missing.", HttpStatus.BAD_REQUEST);
        }
        User user = userService.getCurrent(auth);

        try {
            User.isBlocked(user);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }

        final Trip trip = tripService.findByID(id);

        if (trip == null) {
            return new ResponseEntity<>("Trip with id: " + id + " was not found", HttpStatus.NOT_FOUND);
        }

        if (!Objects.equals(id, tripDTO.getId())) {
            return new ResponseEntity<>("You can modify only your trips.", HttpStatus.FORBIDDEN);
        }

        if (user.getRole() != Role.ADMIN && !trip.getAuthor().getId().equals(user.getId())) {
            return new ResponseEntity<>("Cannot edit trip of another user.", HttpStatus.FORBIDDEN);
        }

        Trip tripToUpdate = mapper.toEntity(tripDTO);

        try {
            tripService.update(trip, tripToUpdate);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }

        LOG.debug("Updated trip {}.", tripDTO);
        return new ResponseEntity<>("Trip was successfully edited.", HttpStatus.OK);
    }

    @PreAuthorize("!anonymous")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createTrip(Authentication auth, @RequestBody TripDTO tripDTO) {
        if (tripDTO == null) {
            return new ResponseEntity<>("Data is missing.", HttpStatus.BAD_REQUEST);
        }
        User user = userService.getCurrent(auth);
        Trip trip = mapper.toEntity(tripDTO);

        try {
            User.isBlocked(user);

            trip.setAuthor(user);
            userService.addTrip(user, trip);
            Attendlist attendlist = attendlistService.create(user, trip);

            LOG.debug("Created trip {}.", trip);
            LOG.debug("Created attendlist {}.", attendlist);

            final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/{id}", trip.getId());
            return new ResponseEntity<>(headers, HttpStatus.CREATED);

        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NO_CONTENT);
        }
    }

    @PreAuthorize("!anonymous")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteTrip(Authentication auth, @PathVariable Integer id) {
        User user = userService.getCurrent(auth);

        try {
            User.isBlocked(user);
            Trip trip = getTrip(id);

            if (user.getRole() != Role.ADMIN && !trip.getAuthor().getId().equals(user.getId())) {
                return new ResponseEntity<>("Cannot delete trip of another user.", HttpStatus.FORBIDDEN);
            }
            userService.removeTrip(user, trip);
            tripService.remove(trip);
            return new ResponseEntity<>("Trip was successfully deleted.", HttpStatus.OK);

        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @PreAuthorize("!anonymous")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TripDTO> getAllActiveTrips() {
        LOG.info("Retrieving all active trips.");
        return tripService.findAllActiveTrips()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @PreAuthorize("!anonymous")
    @GetMapping(value = "/country/{country}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TripDTO> getAllActiveTripsByCountry(@PathVariable String country) {
        LOG.info("Retrieving all active trips by country.");
        return tripService.findAllActiveTrips()
                .stream()
                .filter(trip -> trip.getCountry().equals(country))
                .map(mapper::toDto)
                .toList();
    }

    @PreAuthorize("!anonymous")
    @GetMapping(value = "/author/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TripDTO> getAllActiveTripsByAuthor(@PathVariable String username) {
        LOG.info("Retrieving all active trips by author.");
        return tripService.findAllActiveTrips()
                .stream()
                .filter(trip -> trip.getAuthor().getUsername().equals(username))
                .map(mapper::toDto)
                .toList();
    }

    @PreAuthorize("!anonymous")
    @GetMapping(value = "/capacity/{capacity}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TripDTO> getAllActiveTripsByCapacity(@PathVariable Integer capacity) {
        LOG.info("Retrieving all active trips by capacity.");
        return tripService.findAllActiveTrips()
                .stream()
                .filter(trip -> trip.getCapacity().equals(capacity))
                .map(mapper::toDto)
                .toList();
    }

    @PreAuthorize("!anonymous")
    @GetMapping(value = "/date/{startDate}/{endDate}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TripDTO> getAllActiveTripsByDate(@PathVariable LocalDate startDate, @PathVariable LocalDate endDate) {
        LOG.info("Retrieving all active trips by date.");
        return tripService.findAllActiveTrips()
                .stream()
                .filter(trip -> trip.getStartDate().equals(startDate))
                .filter(trip -> trip.getEndDate().equals(endDate))
                .map(mapper::toDto)
                .toList();
    }

    @PreAuthorize("!anonymous")
    @GetMapping(value = "/current", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TripDTO> getCurrentUserTrips(Authentication auth) {
        User user = userService.getCurrent(auth);
        return tripService.findByAuthor(user)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @PreAuthorize("!anonymous")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public TripDTO getTripByID(@PathVariable Integer id) {
        try {
            Trip trip = getTrip(id);
            return mapper.toDto(trip);
        } catch (NotFoundException e) {
            return null;
        }
    }

    @PreAuthorize("!anonymous")
    @PostMapping(value = "/{tripID}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addComment(Authentication auth, @PathVariable Integer tripID, @RequestBody CommentDTO commentDTO) {
        if (commentDTO == null) {
            return new ResponseEntity<>("Data is missing.", HttpStatus.BAD_REQUEST);
        }

        User user = userService.getCurrent(auth);

        try {
            User.isBlocked(user);
            Trip trip = getTrip(tripID);
            Comment comment = mapper.toEntity(commentDTO);

            comment.setAuthor(user);
            tripService.addComment(trip, comment);
            LOG.debug("Added comment {}.", comment);
            return new ResponseEntity<>(HttpStatus.CREATED);

        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(value = "/{tripID}/{commentID}")
    public ResponseEntity<String> deleteComment(@PathVariable Integer tripID, @PathVariable Integer commentID) {
        Comment comment = commentService.findByID(commentID);
        Trip trip = tripService.findByID(tripID);

        if (comment == null) {
            return new ResponseEntity<>("Comment with id: " + commentID + " was not found.", HttpStatus.NOT_FOUND);
        }
        if (trip == null) {
            return new ResponseEntity<>("Trip with id: " + tripID + " was not found.", HttpStatus.NOT_FOUND);
        }

        tripService.removeComment(trip, comment);
        return new ResponseEntity<>("Comment was successfully deleted.", HttpStatus.OK);
    }


    private Trip getTrip(int id) {
        Trip trip = tripService.findByID(id);
        if (trip == null) {
            throw NotFoundException.create("Trip", id);
        }
        return trip;
    }
}
