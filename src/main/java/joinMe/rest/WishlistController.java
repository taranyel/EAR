package joinMe.rest;

import joinMe.db.entity.Wishlist;
import joinMe.service.UserService;
import joinMe.service.WishlistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
    public void addWishlist(@RequestBody Wishlist wishlist) {

    }

    @DeleteMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public void deleteWishlist(@RequestBody Wishlist wishlist) {

    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Wishlist> getWishlistsOfCurrentUser() {
        return userService.getCurrentUserWishlists();
    }
}
