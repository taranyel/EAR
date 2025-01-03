package joinMe.rest;

import joinMe.db.entity.Trip;
import joinMe.db.entity.User;
import joinMe.db.entity.Wishlist;
import joinMe.db.exception.NotFoundException;
import joinMe.rest.dto.Mapper;
import joinMe.rest.dto.TripDTO;
import joinMe.rest.util.RestUtils;
import joinMe.security.model.UserDetails;
import joinMe.service.UserService;
import joinMe.service.WishlistService;
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
@RequestMapping("/wishlists")
public class WishlistController {

    private static final Logger LOG = LoggerFactory.getLogger(WishlistController.class);

    private final WishlistService wishlistService;

    private final UserService userService;

    private final Mapper mapper;

    @Autowired
    public WishlistController(WishlistService wishlistService, UserService userService, Mapper mapper) {
        this.wishlistService = wishlistService;
        this.userService = userService;
        this.mapper = mapper;
    }

    @PreAuthorize("!anonymous")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createWishlist(Authentication auth, @RequestBody TripDTO tripDTO) {
        User user = userService.getCurrent(auth);
        Trip trip = mapper.toEntity(tripDTO);
        Wishlist wishlist = wishlistService.create(user, trip);
        userService.addWishlist(user, wishlist);

        LOG.debug("Created wishlist {}.", wishlist);
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/{id}", trip.getId());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PreAuthorize("!anonymous")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteWishlist(Authentication auth, @PathVariable int id) {
        User user = userService.getCurrent(auth);
        try {
            Wishlist wishlist = getWishlist(id, user);
            userService.removeWishlist(user, wishlist);
            return new ResponseEntity<>("Wishlist was successfully removed.", HttpStatus.OK);

        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @PreAuthorize("!anonymous")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TripDTO> getAllWishlists(Authentication auth) {
        User user = userService.getCurrent(auth);
        return user.getWishlists().stream()
                .map(Wishlist::getTrip)
                .toList()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @PreAuthorize("!anonymous")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public TripDTO getWishlist(Authentication auth, @PathVariable int id) {
        User user = userService.getCurrent(auth);
        try {
            return mapper.toDto(getWishlist(id, user).getTrip());
        } catch (Exception e) {
            return null;
        }
    }

    private Wishlist getWishlist(int id, User user) {
        Wishlist wishlist = wishlistService.findByID(id);
        if (wishlist == null) {
            throw NotFoundException.create("Wishlist", id);
        }

        if (!wishlist.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("Cannot access wishlist of another user.");
        }
        return wishlist;
    }
}
