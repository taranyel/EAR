package joinMe.rest;

import joinMe.db.entity.Attendlist;
import joinMe.db.entity.Message;
import joinMe.db.entity.User;
import joinMe.db.exception.NotFoundException;
import joinMe.rest.dto.AttendlistDTO;
import joinMe.rest.dto.Mapper;
import joinMe.rest.dto.MessageDTO;
import joinMe.rest.dto.UserDTO;
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

    private final Mapper mapper;

    @Autowired
    public AttendlistController(AttendlistService attendlistService, MessageService messageService, UserService userService, Mapper mapper) {
        this.attendlistService = attendlistService;
        this.messageService = messageService;
        this.userService = userService;
        this.mapper = mapper;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AttendlistDTO> getCurrentUserAttendLists(Authentication auth) {
        assert auth.getPrincipal() instanceof UserDetails;
        final User user = ((UserDetails) auth.getPrincipal()).getUser();
        return user.getAttendlists()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<MessageDTO> getAttendList(Authentication auth, @PathVariable Integer id) {
        assert auth.getPrincipal() instanceof UserDetails;
        final User user = ((UserDetails) auth.getPrincipal()).getUser();
        return getAttendlist(user, id).getMessages()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @GetMapping(value = "/{id}/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserDTO> getAllJoinersOfAttendlist(Authentication auth, @PathVariable Integer id) {
        assert auth.getPrincipal() instanceof UserDetails;
        return userService.getAllJoinersOfAttendlistByID(id)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @PostMapping(value = "/{attendlistID}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> addMessage(Authentication auth, @PathVariable int attendlistID, @RequestBody MessageDTO messageDTO) {
        assert auth.getPrincipal() instanceof UserDetails;
        final User user = ((UserDetails) auth.getPrincipal()).getUser();
        Attendlist attendlist = getAttendlist(user, attendlistID);

        Message message = mapper.toEntity(messageDTO);

        if (attendlistID != message.getAttendlist().getId() || !Objects.equals(message.getAuthor().getId(), user.getId())) {
            throw new AccessDeniedException("Wrong attendlist or you are not author of the message.");
        }

        attendlistService.addMessage(attendlist, message);
        messageService.persist(message);
        LOG.debug("Added message {}.", messageDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/{id}")
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
