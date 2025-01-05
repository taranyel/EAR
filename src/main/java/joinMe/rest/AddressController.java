package joinMe.rest;

import jakarta.validation.Valid;
import joinMe.db.entity.Address;
import joinMe.db.entity.User;
import joinMe.rest.dto.AddressDTO;
import joinMe.rest.dto.Mapper;
import joinMe.rest.dto.UserDTO;
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
    public ResponseEntity<String> changeAddress(Authentication auth,@Valid @RequestBody AddressDTO addressDTO) {
        addressService.validateAddressType(addressDTO.getType());
        User user = userService.getCurrent(auth);
        Address address = mapper.toEntity(addressDTO);

        user.getAddress().removeResident(user);
        addressService.setAddress(address, user);
        userService.update(user);

        LOG.info("User with id: {} has changed address. New address is: {}", user.getId(), address);
        return new ResponseEntity<>(user.toString(), HttpStatus.OK);
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
        User user = userService.getCurrent(auth);
        LOG.info("Retrieving current address.");
        return mapper.toDto(addressService.findByID(user.getAddress().getId()));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/{addressID}/residents", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserDTO> getAllResidents(@PathVariable Integer addressID) {
        Address address = addressService.findByID(addressID);
        LOG.info("Retrieving all residents of address with id: .{}", addressID);
        return address.getResidents()
                .stream()
                .map(mapper::toDto)
                .toList();
    }
}
