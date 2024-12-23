package joinMe.rest;

import joinMe.db.entity.Attendlist;
import joinMe.db.entity.User;
import joinMe.db.exception.NotFoundException;
import joinMe.security.model.UserDetails;
import joinMe.service.AttendlistService;
import joinMe.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/attendlists")
public class AttendlistController {

    private static final Logger LOG = LoggerFactory.getLogger(AttendlistController.class);

    private final AttendlistService attendlistService;

    private final UserService userService;

    @Autowired
    public AttendlistController(AttendlistService attendlistService, UserService userService) {
        this.attendlistService = attendlistService;
        this.userService = userService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Attendlist> getCurrentUserAttendLists() {
        return userService.getCurrentUserAttendLists();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Attendlist getAttendList(Authentication auth, @PathVariable Integer id) {
        final Attendlist attendlist = attendlistService.findByID(id);
        if (attendlist == null) {
            throw NotFoundException.create("Attendlist", id);
        }
        assert auth.getPrincipal() instanceof UserDetails;
        final User user = ((UserDetails) auth.getPrincipal()).getUser();

        if (!attendlist.getJoiner().getId().equals(user.getId())) {
            throw new AccessDeniedException("Cannot access attendlist of another user.");
        }
        return attendlist;
    }
}
