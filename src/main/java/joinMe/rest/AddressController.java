package joinMe.rest;

import joinMe.db.entity.Address;
import joinMe.db.entity.User;
import joinMe.rest.dto.AddressDTO;
import joinMe.rest.dto.Mapper;
import joinMe.rest.dto.UserDTO;
import joinMe.security.model.UserDetails;
import joinMe.service.AddressService;
import joinMe.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/address")
public class AddressController {
    private static final Logger LOG = LoggerFactory.getLogger(AddressController.class);

    private final AddressService addressService;

    private final UserService userService;

    private final Mapper mapper;

    @Autowired
    public AddressController(AddressService addressService, Mapper mapper, UserService userService) {
        this.addressService = addressService;
        this.mapper = mapper;
        this.userService = userService;
    }

    @PreAuthorize("!anonymous")
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<String> changeAddress(Authentication auth, @RequestBody AddressDTO addressDTO) {
        assert auth.getPrincipal() instanceof UserDetails;
        final int userId = ((UserDetails) auth.getPrincipal()).getUser().getId();
        User user = userService.findByID(userId);

        Address address = mapper.toEntity(addressDTO);
        Address existingAddress = addressService.findByAll(address);

        user.getAddress().removeResident(user);

        if (existingAddress == null) {
            address.addResident(user);
            addressService.persist(address);
            user.setAddress(address);
        } else {
            existingAddress.addResident(user);
            addressService.update(existingAddress);
            user.setAddress(existingAddress);
        }

        userService.update(user);

        return new ResponseEntity<>("Address was successfully changed.", HttpStatus.OK);
    }

    @PreAuthorize("!anonymous")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AddressDTO> getAllAddresses() {
        LOG.info("Retrieving all addresses.");
        return addressService.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @PreAuthorize("!anonymous")
    @GetMapping(value = "/current", produces = MediaType.APPLICATION_JSON_VALUE)
    public AddressDTO getCurrentAddress(Authentication auth) {
        assert auth.getPrincipal() instanceof UserDetails;
        final int userId = ((UserDetails) auth.getPrincipal()).getUser().getId();
        User user = userService.findByID(userId);

        LOG.info("Retrieving current address.");
        return mapper.toDto(user.getAddress());
    }

    @PreAuthorize("!anonymous")
    @GetMapping(value = "/{addressID}/residents", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserDTO> getAllResidents(@PathVariable int addressID) {
        LOG.info("Retrieving all residents.");
        Address address = addressService.findByID(addressID);

        if (address == null) {
            return null;
        }

        return address.getResidents()
                .stream()
                .map(mapper::toDto)
                .toList();
    }
}
