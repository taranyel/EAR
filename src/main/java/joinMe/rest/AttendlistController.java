package joinMe.rest;

import joinMe.db.entity.Attendlist;
import joinMe.service.AttendlistService;
import joinMe.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public void addAttendlist(@RequestBody Attendlist attendlist) {

    }

    @DeleteMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public void deleteAttendlist(@RequestBody Attendlist attendlist) {

    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Attendlist> getAttendListsOfCurrentUser() {
        return userService.getCurrentUserAttendLists();
    }
}
