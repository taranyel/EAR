package joinMe.rest;

import joinMe.db.entity.Role;
import joinMe.db.entity.Trip;
import joinMe.db.entity.User;
import joinMe.db.entity.Wishlist;
import joinMe.db.exception.NotFoundException;
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

    @Autowired
    public WishlistController(WishlistService wishlistService, UserService userService) {
        this.wishlistService = wishlistService;
        this.userService = userService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createWishlist(@RequestBody Wishlist wishlist) {
        wishlistService.persist(wishlist);
        LOG.debug("Created wishlist {}.", wishlist);
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/{id}", wishlist.getId());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_GUEST')")
    @DeleteMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void deleteWishlist(Authentication auth, @PathVariable int id) {
        assert auth.getPrincipal() instanceof UserDetails;
        final User user = ((UserDetails) auth.getPrincipal()).getUser();
        Wishlist wishlist = getWishlist(id, user);
        userService.removeWishlist(user, wishlist);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Trip> getAllWishlists() {
        return userService.getCurrentUserWishlists().stream()
                .map(Wishlist::getTrip).toList();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Trip getWishlist(Authentication auth, @PathVariable int id) {
        assert auth.getPrincipal() instanceof UserDetails;
        final User user = ((UserDetails) auth.getPrincipal()).getUser();
        return getWishlist(id, user).getTrip();
    }

    private Wishlist getWishlist(int id, User user) {
        Wishlist wishlist = wishlistService.findByID(id);
        if (wishlist == null) {
            throw NotFoundException.create("Wishlist", id);
        }

        if (user.getRole() != Role.ADMIN && !wishlist.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("Cannot access wishlist of another user.");
        }
        return wishlist;
    }
}
