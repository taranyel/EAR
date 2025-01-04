package joinMe.rest;

import joinMe.db.entity.JoinRequest;
import joinMe.db.entity.Role;
import joinMe.db.entity.Trip;
import joinMe.db.entity.User;
import joinMe.db.exception.NotFoundException;
import joinMe.rest.dto.JoinRequestDTO;
import joinMe.rest.dto.Mapper;
import joinMe.rest.util.RestUtils;
import joinMe.service.JoinRequestService;
import joinMe.service.TripService;
import joinMe.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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

        if (trip == null) {
            return new ResponseEntity<>("Trip with id: " + tripID + " was not found.", HttpStatus.NOT_FOUND);
        }

        try {
            User.isBlocked(user);
            JoinRequest joinRequest = joinRequestService.create(user, trip);
            userService.addJoinRequest(joinRequest);

            final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/{id}", joinRequest.getId());
            return new ResponseEntity<>(joinRequest.toString(), headers, HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    private JoinRequest getJoinRequestForRequester(User user, int id) {
        JoinRequest joinRequest = joinRequestService.findByID(id);
        if (joinRequest == null) {
            throw NotFoundException.create("JoinRequest", id);
        }

        if (user.getRole() != Role.ADMIN && !joinRequest.getRequester().getId().equals(user.getId())) {
            throw new AccessDeniedException("Cannot access join request of another user.");
        }

        return joinRequest;
    }

    private JoinRequest getJoinRequestForApproval(User user, int id) {
        JoinRequest joinRequest = joinRequestService.findByID(id);
        if (joinRequest == null) {
            throw NotFoundException.create("JoinRequest", id);
        }

        if (user.getRole() != Role.ADMIN && !joinRequest.getTrip().getAuthor().getId().equals(user.getId())) {
            throw new AccessDeniedException("You can access join requests only for trips you are author of.");
        }
        return joinRequest;
    }

    @PreAuthorize("!anonymous")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> cancelJoinRequest(Authentication auth, @PathVariable Integer id) {
        User user = userService.getCurrent(auth);

        try {
            User.isBlocked(user);
            JoinRequest joinRequest = getJoinRequestForRequester(user, id);
            userService.cancelJoinRequest(user, joinRequest);
            return new ResponseEntity<>("Join request with id: " + id + " was canceled.", HttpStatus.OK);

        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @PreAuthorize("!anonymous")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<JoinRequestDTO> getAllJoinRequests(Authentication auth) {
        User user = userService.getCurrent(auth);
        return joinRequestService.findByRequester(user)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @PreAuthorize("!anonymous")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public JoinRequestDTO getJoinRequest(Authentication auth, @PathVariable Integer id) {
        User user = userService.getCurrent(auth);
        try {
            return mapper.toDto(getJoinRequestForRequester(user, id));
        } catch (Exception e) {
            return null;
        }
    }

    @PreAuthorize("!anonymous")
    @GetMapping(value = "/forApproval", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<JoinRequestDTO> getAllJoinRequestsForApproval(Authentication auth) {
        User user = userService.getCurrent(auth);
        return joinRequestService.getJoinRequestsForApproval(user)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @PreAuthorize("!anonymous")
    @GetMapping(value = "/forApproval/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public JoinRequestDTO getJoinRequestForApprovalByID(Authentication auth, @PathVariable Integer id) {
        User user = userService.getCurrent(auth);
        try {
            JoinRequest joinRequest = getJoinRequestForApproval(user, id);
            return mapper.toDto(joinRequest);
        } catch (Exception e) {
            return null;
        }
    }

    @PreAuthorize("!anonymous")
    @GetMapping(value = "/approve/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> approveJoinRequest(Authentication auth, @PathVariable Integer id) {
        User user = userService.getCurrent(auth);
        try {
            User.isBlocked(user);
            JoinRequest joinRequest = getJoinRequestForApproval(user, id);
            userService.approveJoinRequest(joinRequest);
            return new ResponseEntity<>("Join request was approved.", HttpStatus.OK);

        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @PreAuthorize("!anonymous")
    @GetMapping(value = "/reject/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> rejectJoinRequest(Authentication auth, @PathVariable Integer id) {
        User user = userService.getCurrent(auth);
        try {
            User.isBlocked(user);
            JoinRequest joinRequest = getJoinRequestForApproval(user, id);
            userService.rejectJoinRequest(joinRequest);
            return new ResponseEntity<>("Join request was rejected.", HttpStatus.OK);

        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }
}
