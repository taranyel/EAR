package joinMe.rest;

import joinMe.db.entity.Attendlist;
import joinMe.db.entity.Message;
import joinMe.db.entity.Trip;
import joinMe.db.entity.User;
import joinMe.db.exception.NotFoundException;
import joinMe.rest.dto.AttendlistDTO;
import joinMe.rest.dto.Mapper;
import joinMe.rest.dto.MessageDTO;
import joinMe.rest.dto.UserDTO;
import joinMe.security.model.UserDetails;
import joinMe.service.AttendlistService;
import joinMe.service.TripService;
import joinMe.service.UserService;
import joinMe.util.MessageComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/attendlists")
public class AttendlistController {

    private static final Logger LOG = LoggerFactory.getLogger(AttendlistController.class);

    private final AttendlistService attendlistService;

    private final TripService tripService;

    private final UserService userService;

    private final Mapper mapper;

    @Autowired
    public AttendlistController(AttendlistService attendlistService, UserService userService,
                                Mapper mapper, TripService tripService) {
        this.attendlistService = attendlistService;
        this.userService = userService;
        this.mapper = mapper;
        this.tripService = tripService;
    }

    @PreAuthorize("!anonymous")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AttendlistDTO> getCurrentUserAttendLists(Authentication auth) {
        assert auth.getPrincipal() instanceof UserDetails;
        final int userId = ((UserDetails) auth.getPrincipal()).getUser().getId();
        User user = userService.findByID(userId);
        return user.getAttendlists()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @PreAuthorize("!anonymous")
    @GetMapping(value = "/{tripID}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<MessageDTO> getAttendList(@PathVariable Integer tripID) {
        Trip trip = tripService.findByID(tripID);

        if (trip == null) {
            return null;
        }

        List<Attendlist> attendlists = attendlistService.findByTrip(trip);
        if (attendlists.isEmpty()) {
            return null;
        }

        return attendlists
                .stream()
                .flatMap(attendlist -> attendlist.getMessages()
                        .stream()
                        .sorted(MessageComparator::compare)
                        .map(mapper::toDto))
                .toList();
    }

    @PreAuthorize("!anonymous")
    @GetMapping(value = "/{tripID}/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserDTO> getAllJoinersOfTrip(Authentication auth, @PathVariable Integer tripID) {
        assert auth.getPrincipal() instanceof UserDetails;
        Trip trip = tripService.findByID(tripID);
        return userService.getAllJoinersOfTrip(trip)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @PreAuthorize("!anonymous")
    @PostMapping(value = "/{tripID}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addMessage(Authentication auth, @PathVariable int tripID, @RequestBody MessageDTO messageDTO) {
        assert auth.getPrincipal() instanceof UserDetails;
        final User user = ((UserDetails) auth.getPrincipal()).getUser();

        Message message = mapper.toEntity(messageDTO);
        Trip trip = tripService.findByID(tripID);

        if (trip == null) {
            return new ResponseEntity<>("Trip with id: " + tripID + " was not found.", HttpStatus.NOT_FOUND);
        }

        try {
            Attendlist attendlist = attendlistService.findByTripAndJoiner(trip, user);

            attendlistService.addMessage(attendlist, message);
            LOG.debug("Added message {}.", messageDTO);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @PreAuthorize("!anonymous")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> leaveAttendlist(Authentication auth, @PathVariable int id) {
        assert auth.getPrincipal() instanceof UserDetails;
        final User user = ((UserDetails) auth.getPrincipal()).getUser();

        try {
            Attendlist attendlist = getAttendlist(user, id);
            userService.leaveAttendlist(user, attendlist);
            return new ResponseEntity<>("Attend list has been leaved.", HttpStatus.OK);

        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    private Attendlist getAttendlist(User user, int id) {
        Attendlist attendlist = attendlistService.findByID(id);
        if (attendlist == null) {
            throw NotFoundException.create("Attendlist", id);
        }

        if (!attendlist.getJoiner().getId().equals(user.getId())) {
            throw new AccessDeniedException("Cannot access attend list of another user.");
        }

        return attendlist;
    }
}
