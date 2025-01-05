package joinMe.rest;

import jakarta.validation.Valid;
import joinMe.db.entity.Address;
import joinMe.db.entity.Rating;
import joinMe.db.entity.User;
import joinMe.rest.dto.Mapper;
import joinMe.rest.dto.RatingDTO;
import joinMe.rest.dto.RegisterDTO;
import joinMe.rest.dto.UserDTO;
import joinMe.rest.util.RestUtils;
import joinMe.service.AddressService;
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
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    private final AddressService addressService;

    private final Mapper mapper;

    @Autowired
    public UserController(UserService userService, Mapper mapper, AddressService addressService) {
        this.userService = userService;
        this.mapper = mapper;
        this.addressService = addressService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> register(@Valid @RequestBody RegisterDTO registerDTO) {
        User user = mapper.toEntity(registerDTO.getUser());

        Address address = mapper.toEntity(registerDTO.getAddress());
        addressService.setAddress(address, user);

        userService.persist(user);
        LOG.info("User \"{}\" successfully registered.", user.getUsername());
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/current");
        return new ResponseEntity<>(user.toString(), headers, HttpStatus.CREATED);
    }

    @PreAuthorize("!anonymous")
    @PutMapping(value = "/current", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateUser(Authentication auth, @Valid @RequestBody UserDTO userDTO) {
        User user = userService.getCurrent(auth);
        User userToUpdate = mapper.toEntity(userDTO);

        if (!userService.isAdmin(user) && !Objects.equals(user.getId(), userToUpdate.getId())) {
            LOG.warn("Cannot change data of another user.");
            return new ResponseEntity<>("Cannot change data of another user.", HttpStatus.FORBIDDEN);
        }

        userService.update(user, userToUpdate);
        LOG.info("User \"{}\" was successfully updated.", userToUpdate.getUsername());
        return new ResponseEntity<>(user.toString(), HttpStatus.OK);
    }

    @PreAuthorize("!anonymous")
    @GetMapping(value = "/current", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDTO getCurrent(Authentication auth) {
        User user = userService.getCurrent(auth);
        LOG.info("User \"{}\" requested their data.", user.getUsername());
        return mapper.toDto(userService.findByID(user.getId()));
    }

    @PreAuthorize("!anonymous")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDTO getUser(@PathVariable Integer id) {
        User user = userService.findByID(id);
        if (user == null) {
            return null;
        }
        return mapper.forOthers(user);
    }

    @PreAuthorize("!anonymous")
    @PostMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> rateUser(Authentication auth, @PathVariable Integer id, @Valid @RequestBody RatingDTO ratingDTO) {
        User current = userService.getCurrent(auth);
        User toRate = userService.findByID(id);

        if (toRate == null) {
            return userNotFound(id);
        }

        if (Objects.equals(current.getId(), toRate.getId())) {
            return new ResponseEntity<>("You can rate only another user.", HttpStatus.FORBIDDEN);
        }

        Rating rating = mapper.toEntity(ratingDTO);
        userService.addRating(toRate, rating);

        return new ResponseEntity<>(toRate.toString(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Integer id) {
        User user = userService.findByID(id);
        if (user == null) {
            return userNotFound(id);
        }
        userService.remove(user);
        LOG.info("User \"{}\" was successfully deleted.", user.getUsername());
        return new ResponseEntity<>("User with id: " + id + " was successfully deleted.", HttpStatus.OK);
    }

    private ResponseEntity<String> userNotFound(Integer id) {
        LOG.warn("User with id: {} was not found.", id);
        return new ResponseEntity<>("User with id: " + id + " was not found", HttpStatus.NOT_FOUND);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{id}/admin")
    public ResponseEntity<String> makeAdmin(@PathVariable Integer id) {
        User user = userService.findByID(id);
        if (user == null) {
            return userNotFound(id);
        }
        userService.setAdmin(user);
        LOG.info("New admin was successfully set.");
        return new ResponseEntity<>("User with id: " + id + " is new admin now.", HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{id}/block")
    public ResponseEntity<String> blockUser(@PathVariable Integer id) {
        User user = userService.findByID(id);
        if (user == null) {
            return userNotFound(id);
        }
        userService.blockUser(user);
        LOG.info("User \"{}\" was blocked.", user.getUsername());
        return new ResponseEntity<>("User with id: " + id + " was blocked.", HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{id}/unblock")
    public ResponseEntity<String> unblockUser(@PathVariable Integer id) {
        User user = userService.findByID(id);
        if (user == null) {
            return userNotFound(id);
        }
        userService.unblockUser(user);
        LOG.info("User \"{}\" was unblocked.", user.getUsername());
        return new ResponseEntity<>("User with id: " + id + " was unblocked.", HttpStatus.OK);
    }
}
