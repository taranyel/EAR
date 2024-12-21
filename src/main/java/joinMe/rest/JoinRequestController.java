package joinMe.rest;

import joinMe.db.entity.JoinRequest;
import joinMe.db.entity.Wishlist;
import joinMe.service.JoinRequestService;
import joinMe.service.UserService;
import joinMe.service.WishlistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/joinRequests")
public class JoinRequestController {

    private static final Logger LOG = LoggerFactory.getLogger(JoinRequestController.class);

    private final JoinRequestService joinRequestService;

    private final UserService userService;

    @Autowired
    public JoinRequestController(JoinRequestService joinRequestService, UserService userService) {
        this.joinRequestService = joinRequestService;
        this.userService = userService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public void addJoinRequest(@RequestBody JoinRequest joinRequest) {

    }

    @DeleteMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public void deleteJoinRequest(@RequestBody JoinRequest joinRequest) {

    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<JoinRequest> getJoinRequestsOfCurrentUser() {
        return userService.getCurrentUserJoinRequests();
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<JoinRequest> getJoinRequestsOfCurrentUserForApproval() {
        return userService.getCurrentUserJoinRequestsForApproval();
    }
}
