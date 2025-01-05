package joinMe.rest;

import jakarta.validation.Valid;
import joinMe.db.entity.Attendlist;
import joinMe.db.entity.Message;
import joinMe.db.entity.Trip;
import joinMe.db.entity.User;
import joinMe.rest.dto.AttendlistDTO;
import joinMe.rest.dto.Mapper;
import joinMe.rest.dto.MessageDTO;
import joinMe.rest.dto.UserDTO;
import joinMe.security.model.UserDetails;
import joinMe.service.AttendlistService;
import joinMe.service.MessageService;
import joinMe.service.TripService;
import joinMe.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/attendlists")
public class AttendlistController {

    private static final Logger LOG = LoggerFactory.getLogger(AttendlistController.class);

    private final AttendlistService attendlistService;

    private final TripService tripService;

    private final UserService userService;

    private final MessageService messageService;

    private final Mapper mapper;

    @Autowired
    public AttendlistController(AttendlistService attendlistService, UserService userService,
                                Mapper mapper, TripService tripService, MessageService messageService) {
        this.attendlistService = attendlistService;
        this.userService = userService;
        this.mapper = mapper;
        this.messageService = messageService;
        this.tripService = tripService;
    }

    @PreAuthorize("!anonymous")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AttendlistDTO> getCurrentUserAttendLists(Authentication auth) {
        User user = userService.getCurrent(auth);
        return attendlistService.findByJoiner(user)
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

        return messageService.findByTrip(trip)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @PreAuthorize("!anonymous")
    @GetMapping(value = "/{tripID}/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserDTO> getAllJoinersOfTrip(Authentication auth, @PathVariable Integer tripID) {
        assert auth.getPrincipal() instanceof UserDetails;
        Trip trip = tripService.findByID(tripID);
        if (trip == null) {
            return null;
        }
        return userService.getAllJoinersOfTrip(trip)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @PreAuthorize("!anonymous")
    @PostMapping(value = "/{tripID}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addMessage(Authentication auth, @PathVariable Integer tripID, @Valid @RequestBody MessageDTO messageDTO) {
        if (messageDTO == null) {
            return new ResponseEntity<>("Data is missing.", HttpStatus.BAD_REQUEST);
        }

        User user = userService.getCurrent(auth);
        Message message = mapper.toEntity(messageDTO);
        Trip trip = tripService.findByID(tripID);

        if (trip == null) {
            return new ResponseEntity<>("Trip with id: " + tripID + " was not found.", HttpStatus.NOT_FOUND);
        }

        UserService.isBlocked(user);

        Attendlist attendlist = attendlistService.findByTripAndJoiner(trip, user);

        if (attendlist == null) {
            return new ResponseEntity<>("You are not joiner of this trip.", HttpStatus.FORBIDDEN);
        }

        attendlistService.addMessage(attendlist, message);
        LOG.debug("Added message {}.", messageDTO);
        return new ResponseEntity<>(attendlist.toString(), HttpStatus.CREATED);

    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(value = "/{tripID}/{messageID}")
    public ResponseEntity<String> deleteMessage(@PathVariable Integer tripID, @PathVariable Integer messageID) {
        Message message = messageService.findByID(messageID);
        Trip trip = tripService.findByID(tripID);

        if (message == null) {
            return new ResponseEntity<>("Message with id: " + messageID + " was not found.", HttpStatus.NOT_FOUND);
        }
        if (trip == null) {
            return new ResponseEntity<>("Trip with id: " + tripID + " was not found.", HttpStatus.NOT_FOUND);
        }

        Attendlist attendlist = attendlistService.findByTripAndJoiner(trip, message.getAuthor());
        attendlistService.removeMessage(attendlist, message);
        return new ResponseEntity<>("Message with id: " + messageID + " was successfully deleted.", HttpStatus.OK);
    }

    @PreAuthorize("!anonymous")
    @DeleteMapping(value = "/leave/{tripID}/{userID}")
    public ResponseEntity<String> leaveAttendlist(Authentication auth, @PathVariable Integer tripID, @PathVariable Integer userID) {
        User currentUser = userService.getCurrent(auth);
        User toLeave = userService.findByID(userID);
        Trip trip = tripService.findByID(tripID);

        if (trip == null) {
            return new ResponseEntity<>("Trip with id: " + tripID + " was not found.", HttpStatus.NOT_FOUND);
        }

        if (toLeave == null) {
            return new ResponseEntity<>("User with id: " + userID + " was not found.", HttpStatus.NOT_FOUND);
        }

        if (!Objects.equals(currentUser.getId(), toLeave.getId()) && !Objects.equals(currentUser.getId(), trip.getAuthor().getId())) {
            return new ResponseEntity<>("To remove other user from chat you need to be admin of this chat.", HttpStatus.FORBIDDEN);
        } else if (currentUser.getId().equals(toLeave.getId()) && Objects.equals(trip.getAuthor().getId(), currentUser.getId())) {
            return new ResponseEntity<>("Admin cannot leave chat.", HttpStatus.FORBIDDEN);
        }

        Attendlist attendlist = attendlistService.findByTripAndJoiner(trip, toLeave);
        if (attendlist == null) {
            return new ResponseEntity<>("User with id: " + userID + " is not joiner of trip with id: " + tripID, HttpStatus.BAD_REQUEST);
        }

        userService.leaveAttendlist(toLeave, attendlist);
        tripService.removeAttendlist(trip, attendlist);

        return new ResponseEntity<>(trip.toString(), HttpStatus.OK);
    }
}
