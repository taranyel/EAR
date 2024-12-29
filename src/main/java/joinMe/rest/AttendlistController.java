package joinMe.rest;

import joinMe.db.entity.*;
import joinMe.db.exception.NotFoundException;
import joinMe.security.model.UserDetails;
import joinMe.service.AttendlistService;
import joinMe.service.MessageService;
import joinMe.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    public AttendlistController(AttendlistService attendlistService, MessageService messageService, UserService userService) {
        this.attendlistService = attendlistService;
        this.messageService = messageService;
        this.userService = userService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Attendlist> getCurrentUserAttendLists(Authentication auth) {
        assert auth.getPrincipal() instanceof UserDetails;
        final User user = ((UserDetails) auth.getPrincipal()).getUser();
        return user.getAttendlists();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Message> getAttendList(Authentication auth, @PathVariable Integer id) {
        assert auth.getPrincipal() instanceof UserDetails;
        final User user = ((UserDetails) auth.getPrincipal()).getUser();
        return getAttendlist(user, id).getMessages();
    }

    @GetMapping(value = "/{id}/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<User> getAllJoinersOfAttendlist(Authentication auth, @PathVariable Integer id) {
        assert auth.getPrincipal() instanceof UserDetails;
        return userService.getAllJoinersOfAttendlistByID(id);
    }

    @PostMapping(value = "/{attendlistID}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> addMessage(Authentication auth, @PathVariable int attendlistID, @RequestBody Message message) {
        assert auth.getPrincipal() instanceof UserDetails;
        final User user = ((UserDetails) auth.getPrincipal()).getUser();
        Attendlist attendlist = getAttendlist(user, attendlistID);

        if (attendlistID != message.getAttendlist().getId() || !Objects.equals(message.getAuthor().getId(), user.getId())) {
            throw new AccessDeniedException("Wrong attendlist or you are not author of the message.");
        }

        attendlistService.addMessage(attendlist, message);
        messageService.persist(message);
        LOG.debug("Added message {}.", message);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void leaveAttendlist(Authentication auth, @PathVariable int id) {
        assert auth.getPrincipal() instanceof UserDetails;
        final User user = ((UserDetails) auth.getPrincipal()).getUser();
        Attendlist attendlist = getAttendlist(user, id);
        userService.leaveAttendlist(user, attendlist);
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
