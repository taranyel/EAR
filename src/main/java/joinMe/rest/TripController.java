package joinMe.rest;

import joinMe.db.entity.Trip;
import joinMe.service.TripService;
import joinMe.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trips")
public class TripController {

    private static final Logger LOG = LoggerFactory.getLogger(TripController.class);

    private final TripService tripService;

    private final UserService userService;

    @Autowired
    public TripController(TripService tripService, UserService userService) {
        this.tripService = tripService;
        this.userService = userService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public void addTrip(@RequestBody Trip trip) {

    }

    @DeleteMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public void deleteTrip(@RequestBody Trip trip) {

    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Trip> getAllActiveTrips() {
        return tripService.findAllActiveTrips();
    }

    @GetMapping(value = "/current", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Trip> getTripsOfCurrentUser() {
        return userService.getCurrentUserTrips();
    }
}
