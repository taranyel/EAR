package joinMe.rest;

import joinMe.rest.dto.Mapper;
import joinMe.rest.dto.UserDTO;
import joinMe.db.entity.User;
import joinMe.rest.util.RestUtils;
import joinMe.security.model.UserDetails;
import joinMe.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    private final Mapper mapper;

    @Autowired
    public UserController(UserService userService, Mapper mapper) {
        this.userService = userService;
        this.mapper = mapper;
    }

    /**
     * Registers a new user.
     *
     * @param userDTO User data
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> register(@RequestBody UserDTO userDTO) {
        User user = mapper.toEntity(userDTO);

        try {
            userService.persist(user);
        } catch (IllegalArgumentException e) {
            final HttpHeaders headers = new HttpHeaders();
            headers.add("Error-Message", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).headers(headers).build();
        }
        LOG.debug("User {} successfully registered.", userDTO);
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/current");
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN') or #userDTO.username == authentication.name")
    public ResponseEntity<Void> updateUser(@PathVariable Long userId, @RequestBody UserDTO userDTO) {
        final Long authenticatedUserId = userService.getId(userDTO.getUsername());

        // Check if the authenticated user is trying to update another user's information
        if (!authenticatedUserId.equals(userId)) {
            LOG.warn("User with ID {} is not authorized to update user with ID {}.", authenticatedUserId, userId);
            final HttpHeaders headers = new HttpHeaders();
            headers.add("Error-Message", "You are not authorized to update this user.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).headers(headers).build();
        }

        User user = mapper.toEntity(userDTO);

        if (!userService.existsById(userId)) {
            LOG.warn("User with ID {} not found.", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        userService.update(userId, user.getFirstName(), user.getLastName(), user.getPassword());
        LOG.debug("User {} successfully updated.", userDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')") // Ensures only admins can access this method
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        final HttpHeaders headers = new HttpHeaders();

        if (userService.delete(id)) {
            LOG.debug("User with ID {} successfully deleted.", id);
            headers.add("INFO-MESSAGE", "User with ID " + id + " successfully deleted.");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).headers(headers).build();
        }
        headers.add("ERROR-MESSAGE", "User with ID " + id + " not found.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_GUEST')")
    @GetMapping(value = "/current", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDTO getCurrent(Authentication auth) {
        assert auth.getPrincipal() instanceof UserDetails;
        return mapper.toDto(((UserDetails) auth.getPrincipal()).getUser());
    }
}
