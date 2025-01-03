package joinMe.rest;

import joinMe.db.entity.Address;
import joinMe.db.entity.User;
import joinMe.rest.dto.Mapper;
import joinMe.rest.dto.RegisterDTO;
import joinMe.rest.dto.UserDTO;
import joinMe.rest.util.RestUtils;
import joinMe.security.model.UserDetails;
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

import java.nio.file.AccessDeniedException;
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
    public ResponseEntity<String> register(@RequestBody RegisterDTO registerDTO) {
        User user = mapper.toEntity(registerDTO.getUser());
        Address address = mapper.toEntity(registerDTO.getAddress());

        Address existingAddress = addressService.findByAll(address);
        if (existingAddress != null) {
            existingAddress.addResident(user);
            addressService.update(existingAddress);
            user.setAddress(existingAddress);
        } else {
            address.addResident(user);
            addressService.persist(address);
            user.setAddress(address);
        }

        try {
            userService.persist(user);
            final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/current");
            return new ResponseEntity<>(headers, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @PreAuthorize("!anonymous")
    @PutMapping(value = "/current", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateUser(Authentication auth, @RequestBody UserDTO userDTO) {
        assert auth.getPrincipal() instanceof UserDetails;
        final User user = ((UserDetails) auth.getPrincipal()).getUser();
        User userToUpdate = mapper.toEntity(userDTO);

        if (!Objects.equals(user.getId(), userDTO.getId())) {
            return new ResponseEntity<>("Cannot change data of another user.", HttpStatus.FORBIDDEN);
        }

        try {
            userService.update(user, userToUpdate);
            LOG.info("Updated user {}.", userDTO);
            return new ResponseEntity<>("User was successfully updated.", HttpStatus.OK);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @PreAuthorize("!anonymous")
    @GetMapping(value = "/current", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDTO getCurrent(Authentication auth) {
        assert auth.getPrincipal() instanceof UserDetails;
        final int userId = ((UserDetails) auth.getPrincipal()).getUser().getId();
        User user = userService.findByID(userId);
        return mapper.toDto(user);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Integer id) {
        User user = userService.findByID(id);

        if (user == null) {
            return new ResponseEntity<>("User with id: " + id + " was not found", HttpStatus.NOT_FOUND);
        }
        userService.remove(user);
        return new ResponseEntity<>("User was successfully deleted.", HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{id}/admin")
    public ResponseEntity<String> makeAdmin(@PathVariable Integer id) {
        User user = userService.findByID(id);

        if (user == null) {
            return new ResponseEntity<>("User with id: " + id + " was not found", HttpStatus.NOT_FOUND);
        }
        userService.setAdmin(user);
        return new ResponseEntity<>("New admin was successfully set.", HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{id}/block")
    public ResponseEntity<String> blockUser(@PathVariable Integer id) {
        User user = userService.findByID(id);

        if (user == null) {
            return new ResponseEntity<>("User with id: " + id + " was not found", HttpStatus.NOT_FOUND);
        }
        userService.blockUser(user);
        return new ResponseEntity<>("User was blocked", HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{id}/unblock")
    public ResponseEntity<String> unblockUser(@PathVariable Integer id) {
        User user = userService.findByID(id);

        if (user == null) {
            return new ResponseEntity<>("User with id: " + id + " was not found", HttpStatus.NOT_FOUND);
        }
        userService.unblockUser(user);
        return new ResponseEntity<>("User was unblocked", HttpStatus.OK);
    }
}
