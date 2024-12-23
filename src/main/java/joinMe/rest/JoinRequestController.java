package joinMe.rest;

import joinMe.db.entity.JoinRequest;
import joinMe.service.JoinRequestService;
import joinMe.service.UserService;
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
    public void createJoinRequest(@RequestBody JoinRequest joinRequest) {

    }

    @DeleteMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public void cancelJoinRequest(@RequestBody JoinRequest joinRequest) {

    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<JoinRequest> getAllJoinRequests() {
        return userService.getCurrentUserJoinRequests();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<JoinRequest> getJoinRequest(@PathVariable Integer id) {

    }

    @GetMapping(value = "/forApproval", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<JoinRequest> getAllJoinRequestsForApproval() {
        return userService.getCurrentUserJoinRequestsForApproval();
    }

    @GetMapping(value = "/forApproval/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<JoinRequest> getJoinRequestForApprovalByID(@PathVariable Integer id) {

    }

    @GetMapping(value = "/approve/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void approveJoinRequest(@PathVariable Integer id) {

    }

    @GetMapping(value = "/reject/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void rejectJoinRequest(@PathVariable Integer id) {

    }
}
