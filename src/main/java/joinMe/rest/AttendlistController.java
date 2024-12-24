package joinMe.rest;

import joinMe.db.entity.*;
import joinMe.db.exception.NotFoundException;
import joinMe.rest.util.RestUtils;
import joinMe.security.model.UserDetails;
import joinMe.service.AttendlistService;
import joinMe.service.MessageService;
import joinMe.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/attendlists")
public class AttendlistController {

    private static final Logger LOG = LoggerFactory.getLogger(AttendlistController.class);

    private final AttendlistService attendlistService;

    private final MessageService messageService;

    private final UserService userService;

    @Autowired
    public AttendlistController(AttendlistService attendlistService, UserService userService, MessageService messageService) {
        this.attendlistService = attendlistService;
        this.userService = userService;
        this.messageService = messageService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Attendlist> getCurrentUserAttendLists() {
        return userService.getCurrentUserAttendLists();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Message> getAttendList(Authentication auth, @PathVariable Integer id) {
        assert auth.getPrincipal() instanceof UserDetails;
        final User user = ((UserDetails) auth.getPrincipal()).getUser();
        return getAttendlist(user, id).getMessages();
    }

    @PostMapping(value = "/{attendlistID}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> addMessage(Authentication auth, @PathVariable int attendlistID, @RequestBody Message message) {
        assert auth.getPrincipal() instanceof UserDetails;
        final User user = ((UserDetails) auth.getPrincipal()).getUser();
        getAttendlist(user, attendlistID);

        if (attendlistID != message.getAttendlist().getId() || !Objects.equals(message.getAuthor().getId(), user.getId())) {
            throw new AccessDeniedException("Wrong attendlist.");
        }
        messageService.persist(message);
        LOG.debug("Added message {}.", message);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    private Attendlist getAttendlist(User user, int id) {
        Attendlist attendlist = attendlistService.findByID(id);
        if (attendlist == null) {
            throw NotFoundException.create("JoinRequest", id);
        }

        if (user.getRole() != Role.ADMIN && !attendlist.getTrip().getAuthor().getId().equals(user.getId())) {
            throw new AccessDeniedException("Cannot access attend list of another user.");
        }

        return attendlist;
    }
}
