package joinMe.rest;

import joinMe.db.entity.JoinRequest;
import joinMe.db.entity.Role;
import joinMe.db.entity.Trip;
import joinMe.db.entity.User;
import joinMe.rest.dto.JoinRequestDTO;
import joinMe.rest.dto.Mapper;
import joinMe.service.JoinRequestService;
import joinMe.service.TripService;
import joinMe.service.UserService;
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
@RequestMapping("/joinRequests")
public class JoinRequestController {

    private static final Logger LOG = LoggerFactory.getLogger(JoinRequestController.class);

    private final JoinRequestService joinRequestService;

    private final UserService userService;

    private final TripService tripService;

    private final Mapper mapper;

    @Autowired
    public JoinRequestController(JoinRequestService joinRequestService, UserService userService, Mapper mapper, TripService tripService) {
        this.joinRequestService = joinRequestService;
        this.userService = userService;
        this.mapper = mapper;
        this.tripService = tripService;
    }

    @PreAuthorize("!anonymous")
    @PostMapping(value = "/{tripID}")
    public ResponseEntity<String> createJoinRequest(Authentication auth, @PathVariable Integer tripID) {
        User user = userService.getCurrent(auth);
        Trip trip = tripService.findByID(tripID);

        UserService.isBlocked(user);
        JoinRequest joinRequest = joinRequestService.create(user, trip);
        userService.addJoinRequest(joinRequest);

        LOG.info("JoinRequest {} was created.", joinRequest);
        return new ResponseEntity<>(joinRequest.toString(), HttpStatus.CREATED);
    }

    private JoinRequest getJoinRequestForRequester(User user, int id) {
        JoinRequest joinRequest = joinRequestService.findByID(id);

        if (user.getRole() != Role.ADMIN && !joinRequest.getRequester().getId().equals(user.getId())) {
            throw new AccessDeniedException("Cannot access join request of another user.");
        }

        LOG.info("Retrieving join request for requester with id: {}.", user.getId());
        return joinRequest;
    }

    private JoinRequest getJoinRequestForApproval(User user, int id) {
        JoinRequest joinRequest = joinRequestService.findByID(id);

        if (user.getRole() != Role.ADMIN && !joinRequest.getTrip().getAuthor().getId().equals(user.getId())) {
            throw new AccessDeniedException("You can access join requests only for trips you are author of.");
        }

        LOG.info("Retrieving join request for approval by user with id: {}.", id);
        return joinRequest;
    }

    @PreAuthorize("!anonymous")
    @DeleteMapping(value = "/{joinRequestID}")
    public ResponseEntity<String> cancelJoinRequest(Authentication auth, @PathVariable Integer joinRequestID) {
        User user = userService.getCurrent(auth);
        UserService.isBlocked(user);
        JoinRequest joinRequest = getJoinRequestForRequester(user, joinRequestID);
        userService.cancelJoinRequest(user, joinRequest);

        LOG.info("JoinRequest with id: {} was cancelled.", joinRequestID);
        return new ResponseEntity<>("Join request with id: " + joinRequestID + " was canceled.", HttpStatus.OK);
    }

    @PreAuthorize("!anonymous")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<JoinRequestDTO> getAllJoinRequests(Authentication auth) {
        User user = userService.getCurrent(auth);
        LOG.info("Retrieving all join requests for user with id: {}.", user.getId());
        return joinRequestService.findByRequester(user)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @PreAuthorize("!anonymous")
    @GetMapping(value = "/{joinRequestID}", produces = MediaType.APPLICATION_JSON_VALUE)
    public JoinRequestDTO getJoinRequest(Authentication auth, @PathVariable Integer joinRequestID) {
        User user = userService.getCurrent(auth);
        LOG.info("Retrieving join request with id: {} for user with id: {}.", joinRequestID, user.getId());
        return mapper.toDto(getJoinRequestForRequester(user, joinRequestID));
    }

    @PreAuthorize("!anonymous")
    @GetMapping(value = "/forApproval", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<JoinRequestDTO> getAllJoinRequestsForApproval(Authentication auth) {
        User user = userService.getCurrent(auth);
        LOG.info("Retrieving all join requests for approval by user with id: {}.", user.getId());
        return joinRequestService.getJoinRequestsForApproval(user)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @PreAuthorize("!anonymous")
    @GetMapping(value = "/forApproval/{joinRequestID}", produces = MediaType.APPLICATION_JSON_VALUE)
    public JoinRequestDTO getJoinRequestForApprovalByID(Authentication auth, @PathVariable Integer joinRequestID) {
        User user = userService.getCurrent(auth);
        JoinRequest joinRequest = getJoinRequestForApproval(user, joinRequestID);
        LOG.info("Retrieving join request with id: {} for approval by user with id: {}.", joinRequestID, user.getId());
        return mapper.toDto(joinRequest);
    }

    @PreAuthorize("!anonymous")
    @GetMapping(value = "/approve/{joinRequestID}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> approveJoinRequest(Authentication auth, @PathVariable Integer joinRequestID) {
        User user = userService.getCurrent(auth);
        UserService.isBlocked(user);
        JoinRequest joinRequest = getJoinRequestForApproval(user, joinRequestID);
        userService.approveJoinRequest(joinRequest);
        LOG.info("JoinRequest with id: {} was approved by user with id: {}.", joinRequestID, user.getId());
        return new ResponseEntity<>("Join request was approved.", HttpStatus.OK);
    }

    @PreAuthorize("!anonymous")
    @GetMapping(value = "/reject/{joinRequestID}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> rejectJoinRequest(Authentication auth, @PathVariable Integer joinRequestID) {
        User user = userService.getCurrent(auth);
        UserService.isBlocked(user);
        JoinRequest joinRequest = getJoinRequestForApproval(user, joinRequestID);
        userService.rejectJoinRequest(joinRequest);
        LOG.info("Join request with id: {} was rejected by user with id: {}.", joinRequestID, user.getId());
        return new ResponseEntity<>("Join request was rejected.", HttpStatus.OK);
    }
}
